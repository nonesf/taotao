package com.taotao.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.ApiService;
import com.taotao.web.pojo.TbCart;
import com.taotao.web.pojo.TbUser;
import com.taotao.web.threaLocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private ApiService apiService ;

    @Value("${TAOTAO_CART_URL}")
    private  String TAOTAO_CART_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    /**
     * 根据购物车系统对外提供的服务接口查询购物车列表
     *
     * @return
     */
    public List<TbCart> queryCartList(){
        try {
            TbUser user = UserThreadLocal.get();
            String url = TAOTAO_CART_URL + "/service/api/cart/" + user.getId();
            String jsonData = apiService.doGet(url);

            if(StringUtils.isNotEmpty(jsonData)){
                return MAPPER.readValue(jsonData, MAPPER.getTypeFactory().constructCollectionType(List.class ,TbCart .class ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null ;
    }
}
