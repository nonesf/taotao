package com.taotao.sso.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.RedisService;
import com.taotao.sso.mapper.TbUserMapper;
import com.taotao.sso.pojo.TbUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Date;


@Service
public class UserService {
    @Autowired
    private TbUserMapper userMapper ;
    @Autowired
    private RedisService redisService ;

    private static final ObjectMapper MAPPER = new ObjectMapper() ;

    private static final Integer REDIS_TIME = 60 * 30;

    public Boolean check(String param, Integer type) {
        TbUser user = new TbUser() ;
        switch (type ){
            case 1 :
                user .setUsername(param );
                break;
            case 2 :
                user .setPhone(param );
                break;
            case 3 :
                user .setEmail(param );
                break;
            default :
                return null ;
        }
        return userMapper.selectByTypeAndParam(user)== null ;
    }

    public Boolean doRegister(TbUser user) {
        user.setId(null) ;
        user.setCreated(new Date()) ;
        user.setUpdated(user.getCreated()) ;
        //密码加密处理

        user.setPassword(DigestUtils.md5Hex(user.getPassword()) );
        return userMapper.insertSelective(user)==1;
    }


    public String doLogin(String username, String password) throws JsonProcessingException {
        TbUser user = new TbUser() ;
        user .setUsername(username);

        TbUser record = userMapper.selectByUserName(user);
        if(null == record ){
            //查询结果不存在
            return null ;
        }

        //对比密码是否正确
        if(!StringUtils.equals(record .getPassword(),DigestUtils.md5Hex(password))){
            return null ;
        }

        //登录成功，生成token,token就是相当于sessionID的作用，使服务端能够识别用户
        String token = DigestUtils .md5Hex(System .currentTimeMillis() + username);

        //将数据保存到redis中,但是password字段不要保存
        redisService.set("TOKEN_" + token,MAPPER .writeValueAsString(record ),REDIS_TIME) ;
        return token ;

    }

    public TbUser queryUserByToken(String token) {
        try {
            String key = "TOKEN_" + token ;
            String jsonData = redisService.get(key) ;
            if(StringUtils .isEmpty(jsonData )){
                //不存在登录信息
                return null ;
            }

            /**
             * 用户关闭浏览器就会将cookie中的token删除，
             * 当查询token存在的时候就说明用户在使用浏览器，
             * 所以重新设置token的生存时间
             */
            redisService.expire(key, REDIS_TIME );
            return MAPPER .readValue(jsonData, TbUser .class );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }
}
