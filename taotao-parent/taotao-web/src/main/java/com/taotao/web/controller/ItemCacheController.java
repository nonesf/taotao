package com.taotao.web.controller;

import com.taotao.common.service.RedisService;
import com.taotao.web.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("item/cache")
public class ItemCacheController {
    @Autowired
    private RedisService redisService ;

    /**
     * 接收商品id，删除对应的商品缓存
     * @param itemId
     * @return
     */
    @RequestMapping(value = "${itemId}", method = RequestMethod.POST)
    public ResponseEntity<Void > deleteCache(@PathVariable("itemId")Long itemId){
        try {
            //删除商品中的缓存
            String key = ItemService.REDIS_KEY + itemId ;
            redisService.del(key);
            return ResponseEntity .status(HttpStatus.NO_CONTENT).body(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity .status(HttpStatus.INTERNAL_SERVER_ERROR ).body(null);
    }

}
