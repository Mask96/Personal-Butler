package com.example.personalbutler;

import com.example.personalbutler.dto.UserEntity;
import com.example.personalbutler.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
class PersonalButlerApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void jpaLoads() {
        List<UserEntity> userEntityList = userRepository.findAll();
        Assertions.assertTrue(userEntityList.size() > 0);
    }
}
