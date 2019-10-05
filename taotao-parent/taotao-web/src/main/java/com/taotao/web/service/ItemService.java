package com.taotao.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.ApiService;
import com.taotao.common.service.RedisService;
import com.taotao.manage.pojo.TbItem;
import com.taotao.manage.pojo.TbItemDesc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class ItemService {

    @Autowired
    private ApiService apiService;

    @Autowired
    private RedisService redisService ;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public  static final String REDIS_KEY = "TAOTAO_WEB_ITEM_DETAIL_";

    private static final Integer REDIS_TIME = 60*60*24;

    @Value("${TAOTAO_MANAGE_URL}")
    private String TAOTAO_MANAGE_URL;
    /**
     * 根据商品id查询商品数据
     *
     * 通过后台系统提供的接口服务进行查询
     */
    public TbItem queryItemById(Long itemId) {

        //从缓存中命中
        //为每一个item创建一个key，分别保存
        String key = REDIS_KEY + itemId ;
        try {
            String cacheData = redisService .get(key);
            if(StringUtils.isNotEmpty(cacheData)){
                //命中，返回
                return MAPPER.readValue(cacheData, TbItem.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = TAOTAO_MANAGE_URL + "/rest/api/item/" + itemId;
        try {
            String jsonData = apiService.doGet(url);
            if(StringUtils.isEmpty(jsonData)){
                return null;
            }

            try {
                //写缓存
                redisService.set(key, jsonData, REDIS_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //将json数据转换为TbItem对象
            return MAPPER.readValue(jsonData, TbItem.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public TbItemDesc queryItemDescById(Long itemId) {
        String key = REDIS_KEY + itemId ;
        try {
            String cacheData = redisService .get(key);
            if(StringUtils.isNotEmpty(cacheData)){
                //命中，返回
                return MAPPER.readValue(cacheData, TbItemDesc.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String url = TAOTAO_MANAGE_URL + "/rest/item/desc/" +itemId ;
            String jsonData = apiService.doGet(url);
            if(StringUtils.isEmpty(jsonData)){
                return null;
            }

            try {
                //写缓存
                redisService.set(key, jsonData, REDIS_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return MAPPER.readValue(jsonData, TbItemDesc.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
