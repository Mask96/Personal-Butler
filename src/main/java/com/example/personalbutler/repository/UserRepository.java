package com.example.personalbutler.repository;

import com.example.personalbutler.dto.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: Mask
 * @time: 2020/9/16 5:31 下午
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUserName(String userName);

    boolean existsByUserName(String userName);

    UserEntity getByUserNameAndUserPassword(String userName, String userPassword);
}
