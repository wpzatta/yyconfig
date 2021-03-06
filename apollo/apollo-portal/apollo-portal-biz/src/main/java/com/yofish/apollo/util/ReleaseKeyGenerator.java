/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.apollo.util;


import com.yofish.apollo.domain.AppEnvClusterNamespace;
import common.utils.UniqueKeyGenerator;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class ReleaseKeyGenerator extends UniqueKeyGenerator {


  /**
   * Generate the release key in the format: timestamp+appCode+cluster+appNamespace+hash(ipAsInt+counter)
   *
   * @param namespace the appNamespace of the release
   * @return the unique release key
   */
  public static String generateReleaseKey(AppEnvClusterNamespace namespace) {
    return generate(namespace.getAppEnvCluster().getApp().getAppCode(), namespace.getAppEnvCluster().getName(), namespace.getAppNamespace().getName());
  }
}
