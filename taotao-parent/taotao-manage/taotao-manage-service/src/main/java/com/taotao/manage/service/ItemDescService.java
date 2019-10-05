package com.taotao.manage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.RedisService;
import com.taotao.manage.mapper.TbItemDescMapper;
import com.taotao.manage.pojo.TbItemDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public abstract class ItemDescService {
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    @Autowired(required = false )
    private RedisService redisService ;

    private static final String REDIS_KEY = "TAOTAO_WEB_ITEM_DETAIL";

    private static final Integer REDIS_TIME = 60*60*24;

    private static final ObjectMapper MAPPER = new ObjectMapper() ;

    /**
     * 新增数据  返回成功的条数
     * @param itemDesc
     * @return
     */
    public Integer saveItem(TbItemDesc itemDesc){
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(itemDesc.getCreated());
        return tbItemDescMapper.insert(itemDesc);

    }


    /**
     * 根据商品id查询商品描述
     * @param itemId
     * @return
     */
    public TbItemDesc queryItemDescById(Long itemId){

        /*try {
            //从缓存中命中，如果命中就返回，没有命中就继续查询，然后将结果写到缓存中
            //定义key的规则
            String key = REDIS_KEY;
            String cacheDate = this.redisService.get(key);
            if (StringUtils.isNotEmpty(cacheDate)) {
                //命中，返回
                //将数据返回为指定类型的数据
                return MAPPER.readValue(cacheDate, TbItemDesc.class);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }*/

        TbItemDesc tbItemDesc = tbItemDescMapper.queryItemDescById(itemId);

        /*//将结果写到缓存中
        try {
            this.redisService.set(REDIS_KEY,MAPPER.writeValueAsString(tbItemDesc),REDIS_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return tbItemDesc;
    }


}
