package com.example.personalbutler.controllers;

import com.alibaba.fastjson.JSON;
import com.example.personalbutler.dto.UserEntity;
import com.example.personalbutler.dto.UserTokenEntity;
import com.example.personalbutler.exception.ContainerException;
import com.example.personalbutler.repository.UserRepository;
import com.example.personalbutler.service.UserService;
import com.example.personalbutler.service.UserTokenService;
import com.example.personalbutler.util.AuthenticationFilter;
import com.example.personalbutler.util.Md5Util;
import com.example.personalbutler.viewModel.PostLoginModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @description:
 * @author: Mask
 * @time: 2020/9/17 4:44 下午
 */

@RestController
@RequestMapping("/api/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public UserEntity login(@RequestBody PostLoginModel postLoginModel, HttpSession session, HttpServletRequest request) throws ContainerException {
        UserEntity userEntity = userService.checkLogin(postLoginModel.getUserName(), postLoginModel.getUserPassword());
        logger.debug("username:{},password:{}", postLoginModel.getUserName(), postLoginModel.getUserPassword());
        if (userEntity != null) {
            logger.debug("log in success");
            // update last login time to now
            userService.updateLastLoginTime(userEntity.getUserId());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if (postLoginModel.getRememberMe()) {
                calendar.add(Calendar.DATE, 7);
            } else {
                calendar.add(Calendar.MINUTE, 30);
            }
            // generate token
            UserTokenEntity userTokenEntity = userTokenService.generateToken(userEntity, postLoginModel.getDeviceType(), AuthenticationFilter.getDeviceId(request), calendar.getTime());
            logger.debug("generate token success:{}", userTokenEntity.getToken());

            // put userEntity to session
            session.setAttribute("user", userEntity);
            return userEntity;
        } else {
            logger.error("log in failed");
            if (userService.existUsername(postLoginModel.getUserName())) {
                throw new ContainerException("密码错误", HttpStatus.BAD_REQUEST);
            } else {
                throw new ContainerException("用户不存在", HttpStatus.BAD_REQUEST);
            }

        }
    }

    @PostMapping("/create")
    @Transactional
    public UserEntity createUser(@RequestBody UserEntity postUser) throws ContainerException {
        if (postUser.getUserName().isEmpty() || postUser.getUserName() == null) {
            throw new ContainerException("请填写手机号", HttpStatus.BAD_REQUEST);
        }
        if (postUser.getUserPassword().isEmpty() || postUser.getUserPassword() == null) {
            throw new ContainerException("请填写用户密码", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByUserName(postUser.getUserName())) {
            throw new ContainerException("用户名已存在", HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(postUser.getUserName());
        userEntity.setUserPassword(Md5Util.md5(postUser.getUserPassword()));
        userEntity.setCreateTime(new Date());
        userEntity.setNickName(postUser.getNickName());
        userRepository.save(userEntity);
        logger.info(MessageFormat.format("create user success! user: {0}", JSON.toJSON(userEntity)));
        return userEntity;
    }
}
