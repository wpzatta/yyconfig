package com.yofish.apollo.repository;

import com.yofish.apollo.domain.AppNamespace4Public;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author WangSongJun
 * @date 2019-12-13
 */
@Repository
public interface AppNamespace4PublicRepository extends JpaRepository<AppNamespace4Public, Long> {
}
