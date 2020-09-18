package com.example.personalbutler.repository;

import com.example.personalbutler.dto.UserTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * @description:
 * @author: Mask
 * @time: 2020/9/18 3:38 下午
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserTokenEntity, Integer> {
    @Query(value = "select * from user_token where token = ?1 and device_id = ?2 and expire >= now() limit 1", nativeQuery = true)
    UserTokenEntity checkToken(String token, String device_id);

    @Transactional
    int deleteByToken(String token);
}
