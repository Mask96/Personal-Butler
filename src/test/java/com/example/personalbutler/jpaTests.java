package com.example.personalbutler;

import com.example.personalbutler.dto.UserEntity;
import com.example.personalbutler.repository.UserRepository;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description:
 * @author: Mask
 * @time: 2020/9/17 5:31 下午
 */
@SpringBootTest
@Ignore
public class jpaTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveUserTest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName("admin");
        userEntity.setUserPassword("admin");
        userEntity.setNickName("管理员");
        userRepository.save(userEntity);
        System.out.println("save success");
    }
}
