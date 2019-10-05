package com.taotao.web.rabbitHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.RedisService;
import com.taotao.web.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;


public class ItemMQHandler {

    @Autowired
    private RedisService redisService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 根据商品id删除redis中的缓存数据
     * 后台系统对商品数据进行增删改的时候将商品id和routkey发给消息队列，
     * 前台系统读取消息，然后将缓存数据删除，
     * 下次再次查询的时候缓存中没有数据就会再次将新的数据写入缓存实现数据同步
     *
     * @param msg
     */
    public void execute(String msg){
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            Long itemId = jsonNode.get("itemId").asLong();

            //从缓存中删除数据
            String key = ItemService.REDIS_KEY + itemId;
            redisService.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
