package com.example.personalbutler.service;

import com.example.personalbutler.dto.UserEntity;
import com.example.personalbutler.repository.UserRepository;
import com.example.personalbutler.util.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description: 用户服务中心
 * @author: Mask
 * @time: 2020/9/16 5:56 下午
 */
@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public UserEntity checkLogin(String userName, String password) {
        String passwordMd5 = Md5Util.md5(password);
        return userRepository.getByUserNameAndUserPassword(userName, passwordMd5);
    }

    public boolean existUsername(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Transactional
    public void updateLastLoginTime(int userId) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (userEntityOptional.isPresent()) {
            userEntityOptional.get().setLastLoginTime(new Date());
            userRepository.save(userEntityOptional.get());
        } else {
            logger.error("updateLastLoginTime,userId :" + userId);
        }
    }
    
}
