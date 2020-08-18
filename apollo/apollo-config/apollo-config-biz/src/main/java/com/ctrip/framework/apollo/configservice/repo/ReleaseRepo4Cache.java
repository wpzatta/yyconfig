package com.ctrip.framework.apollo.configservice.repo;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.yofish.apollo.component.constant.PermissionType;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.service.ReleaseMessageService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.apollo.component.util.ReleaseMessageKeyGenerator;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.dto.ApolloNotificationMessages;
import framework.apollo.tracer.Tracer;
import framework.apollo.tracer.spi.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/4/15 上午10:59
 */
public class ReleaseRepo4Cache implements ReleaseRepo {
    private static final Logger logger = LoggerFactory.getLogger(ReleaseRepo4Cache.class);
    private static final long DEFAULT_EXPIRED_AFTER_ACCESS_IN_MINUTES = 60;//1 hour
    private static final String TRACER_EVENT_CACHE_INVALIDATE = "ConfigCache.Invalidate";
    private static final String TRACER_EVENT_CACHE_LOAD = "ConfigCache.LoadFromDB";
    private static final String TRACER_EVENT_CACHE_LOAD_ID = "ConfigCache.LoadFromDBById";
    private static final String TRACER_EVENT_CACHE_GET = "ConfigCache.Get";
    private static final String TRACER_EVENT_CACHE_GET_ID = "ConfigCache.GetById";
    private static final Splitter STRING_SPLITTER =
            Splitter.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).omitEmptyStrings();

    @Autowired
    private ReleaseService releaseService;

    @Autowired
    private ReleaseMessageService releaseMessageService;

    private LoadingCache<String, ReleaseRepo4Cache.ConfigCacheEntry> configCache;

    private LoadingCache<Long, Optional<Release>> configIdCache;

    private ReleaseRepo4Cache.ConfigCacheEntry nullConfigCacheEntry;

    public ReleaseRepo4Cache() {
        nullConfigCacheEntry = new ReleaseRepo4Cache.ConfigCacheEntry(ConfigConsts.NOTIFICATION_ID_PLACEHOLDER, null);
    }

    @PostConstruct
    void initialize() {
        configCache = CacheBuilder.newBuilder()
                .expireAfterAccess(DEFAULT_EXPIRED_AFTER_ACCESS_IN_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<String, ReleaseRepo4Cache.ConfigCacheEntry>() {
                    @Override
                    public ReleaseRepo4Cache.ConfigCacheEntry load(String key) throws Exception {
                        List<String> namespaceInfo = STRING_SPLITTER.splitToList(key);
                        if (namespaceInfo.size() != 4) {
                            Tracer.logError(
                                    new IllegalArgumentException(String.format("Invalid cache load key %s", key)));
                            return nullConfigCacheEntry;
                        }

                        Transaction transaction = Tracer.newTransaction(TRACER_EVENT_CACHE_LOAD, key);
                        try {
                            ReleaseMessage latestReleaseMessage = releaseMessageService.findLatestReleaseMessageForMessages(Lists.newArrayList(key));
                            Release latestRelease = releaseService.findLatestActiveRelease(namespaceInfo.get(0), namespaceInfo.get(1), namespaceInfo.get(2), namespaceInfo.get(3));

                            transaction.setStatus(Transaction.SUCCESS);

                            long notificationId = latestReleaseMessage == null ? ConfigConsts.NOTIFICATION_ID_PLACEHOLDER : latestReleaseMessage
                                    .getId();

                            if (notificationId == ConfigConsts.NOTIFICATION_ID_PLACEHOLDER && latestRelease == null) {
                                return nullConfigCacheEntry;
                            }

                            return new ReleaseRepo4Cache.ConfigCacheEntry(notificationId, latestRelease);
                        } catch (Throwable ex) {
                            transaction.setStatus(ex);
                            throw ex;
                        } finally {
                            transaction.complete();
                        }
                    }
                });
        configIdCache = CacheBuilder.newBuilder()
                .expireAfterAccess(DEFAULT_EXPIRED_AFTER_ACCESS_IN_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<Long, Optional<Release>>() {
                    @Override
                    public Optional<Release> load(Long key) throws Exception {
                        Transaction transaction = Tracer.newTransaction(TRACER_EVENT_CACHE_LOAD_ID, String.valueOf(key));
                        try {
                            Release release = releaseService.findActiveOne(key);

                            transaction.setStatus(Transaction.SUCCESS);

                            return Optional.ofNullable(release);
                        } catch (Throwable ex) {
                            transaction.setStatus(ex);
                            throw ex;
                        } finally {
                            transaction.complete();
                        }
                    }
                });
    }

    @Override
    public Release findActiveOne(long id, ApolloNotificationMessages clientMessages) {
        Tracer.logEvent(TRACER_EVENT_CACHE_GET_ID, String.valueOf(id));
        return configIdCache.getUnchecked(id).orElse(null);
    }

    @Override
    public Release findLatestActiveRelease(String appId, String clusterName, String env, String namespaceName,
                                              ApolloNotificationMessages clientMessages) {
        String key = ReleaseMessageKeyGenerator.generate(appId, clusterName, env, namespaceName);

        Tracer.logEvent(TRACER_EVENT_CACHE_GET, key);

        ReleaseRepo4Cache.ConfigCacheEntry cacheEntry = configCache.getUnchecked(key);

        //cache is out-dated
        if (clientMessages != null && clientMessages.has(key) &&
                clientMessages.get(key) > cacheEntry.getNotificationId()) {
            //invalidate the cache and try to load from db again
            invalidate(key);
            cacheEntry = configCache.getUnchecked(key);
        }

        return cacheEntry.getRelease();
    }

    private void invalidate(String key) {
        configCache.invalidate(key);
        Tracer.logEvent(TRACER_EVENT_CACHE_INVALIDATE, key);
    }

    @Override
    public void onReceiveReleaseMessage(ReleaseMessage message, String channel) {
        logger.info("message received - channel: {}, message: {}", channel, message);
        if (!PermissionType.Topics.APOLLO_RELEASE_TOPIC.equals(channel) || Strings.isNullOrEmpty(message.getMessage())) {
            return;
        }

        try {
            invalidate(message.getMessage());

            //warm up the cache
            configCache.getUnchecked(message.getMessage());
        } catch (Throwable ex) {
            //ignore
        }
    }


    private static class ConfigCacheEntry {
        private final long notificationId;
        private final Release release;

        public ConfigCacheEntry(long notificationId, Release release) {
            this.notificationId = notificationId;
            this.release = release;
        }

        public long getNotificationId() {
            return notificationId;
        }

        public Release getRelease() {
            return release;
        }
    }
}