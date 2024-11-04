package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.Uservice;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserViceImpl implements Uservice {
    private static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    @Override
    public User wxlogin(UserLoginDTO userLoginDTO) {
        //调用微信接口，获取当前用户的openid
        String openid = getOpenid(userLoginDTO);
        //判断openid是否为空
        if (openid==null||openid.equals("")){
throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断是否为新用户,自动zhuc
       User user= userMapper.getByOpenId(openid);
        if (user==null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //
        return user;
    }
//ctrl+alt+m
    private String getOpenid(UserLoginDTO userLoginDTO) {
        Map<String, String> map=new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("grant_type", "authorization_code");
        map.put("js_code", userLoginDTO.getCode());

        String s = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(s);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
