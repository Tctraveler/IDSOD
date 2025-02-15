package com.Tc_traveler.IDSOD.service.impl;

import com.Tc_traveler.IDSOD.entity.User;
import com.Tc_traveler.IDSOD.mapper.UserMapper;
import com.Tc_traveler.IDSOD.service.UserService;
import com.Tc_traveler.IDSOD.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    @Override
    public void register(String username, String firstPassword) {
        String md5Pwd = Md5Util.getMD5String(firstPassword);
        userMapper.register(username,md5Pwd);
    }
}
