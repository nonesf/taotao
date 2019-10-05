package com.taotao.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.ApiService;
import com.taotao.web.pojo.TbUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    @Autowired
    private ApiService apiService;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Value("${TAOTAO_SSO_URL}")
    public String TAOTAO_SSO_URL;

    /**
     * 根据token查询用户信息
     *
     * @param token
     * @return
     */
    public TbUser queryByToken(String token){
        String url = TAOTAO_SSO_URL + "/service/user/" + token ;
        try {
            String jsonData = apiService.doGet(url);
            if(StringUtils.isNotEmpty(jsonData)){
                //不为空说明已经登录
                return MAPPER.readValue(jsonData, TbUser.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
