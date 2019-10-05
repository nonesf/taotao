package com.taotao.cart.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.cart.bean.TbItem;
import com.taotao.common.service.ApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class ItemService {

    @Autowired
    private ApiService apiService ;

    @Value("${TAOTAO_MANAGE_URL}")
    private String TAOTAO_MANAGE_URL ;

    private static final ObjectMapper MAPPER = new ObjectMapper() ;

    /**
     * 通过后台系统查询商品基本数据
     *
     * @param itemId
     * @return
     */
    public TbItem queryItemById(Long itemId){
        try {
            String url = TAOTAO_MANAGE_URL + "/rest/api/item/" + itemId;
            String jsonData = apiService.doGet(url);
            if(StringUtils .isNotEmpty(jsonData)){
                return MAPPER.readValue(jsonData, TbItem.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
