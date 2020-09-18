package com.example.personalbutler.service;

import com.example.personalbutler.dto.UserEntity;
import com.example.personalbutler.dto.UserTokenEntity;
import com.example.personalbutler.repository.UserRepository;
import com.example.personalbutler.repository.UserTokenRepository;
import com.example.personalbutler.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.UUID;

/**
 * @description: 用户token服务，包括登录时token相关操作
 * @author: Mask
 * @time: 2020/9/18 3:37 下午
 */
@Service
public class UserTokenService {
    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserTokenEntity generateToken(UserEntity user, String deviceType, String deviceId, Date expire) {
        String token = Md5Util.md5(UUID.randomUUID().toString());
        UserTokenEntity userTokenEntity = new UserTokenEntity();
        userTokenEntity.setUserName(user.getUserName());
        userTokenEntity.setToken(token);
        userTokenEntity.setDeviceId(deviceId);
        userTokenEntity.setDeviceType(deviceType);
        userTokenEntity.setExpire(expire);
        userTokenEntity.setCreateTime(new Date());
        return userTokenRepository.save(userTokenEntity);
    }

    public UserEntity checkToken(String token, String deviceId) {
        UserTokenEntity userTokenEntity = userTokenRepository.checkToken(token, deviceId);
        if (userTokenEntity != null) {
            UserEntity user = userRepository.findByUserName(userTokenEntity.getUserName());
            return user;
        }
        return null;
    }

    public int deleteToken(String token) {
        return userTokenRepository.deleteByToken(token);
    }
}
