package com.taotao.manage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.taotao.common.bean.EasyUIResult;
import com.taotao.common.service.ApiService;
import com.taotao.common.service.RedisService;
import com.taotao.manage.mapper.TbItemDescMapper;
import com.taotao.manage.mapper.TbItemMapper;
import com.taotao.manage.pojo.TbItem;
import com.taotao.manage.pojo.TbItemDesc;
import com.taotao.manage.pojo.TbItemExample;
import com.taotao.manage.pojo.TbItemParamItem;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService extends ItemDescService{
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private ItemDescService itemDescService;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Autowired
    private ItemParamItemService itemParamItemService ;
    @Autowired
    private RedisService redisService ;
    @Autowired
    private ApiService apiService ;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    private static final String REDIS_KEY = "TAOTAO_WEB_ITEM_DETAIL_";

    private static final Integer REDIS_TIME = 60*60*24;

    @Value("${TAOTAO_WEB_URL}")
    private String TAOTAO_WEB_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper() ;

    public boolean saveItem(TbItem tbItem , String desc , String itemParams){
        tbItem.setCreated(new Date());
        tbItem.setUpdated(tbItem.getCreated());

        //初始值
        tbItem.setStatus((byte) 1);
        //出于安全考虑，强制设置id为null，通过数据库自增长得到
        tbItem.setId(null);
        Integer count1 = tbItemMapper.insert(tbItem);

        //保存商品数据
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemId( tbItemMapper.getId());
        itemDesc.setItemDesc(desc);
        Integer count2 = itemDescService.saveItem(itemDesc);

        //保存商品规格参数
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        //因为是新new的对象，其id为null，因此可以不必强制设置
        tbItemParamItem.setItemId(tbItem.getId());
        tbItemParamItem.setParamData(itemParams);
        Integer count3 = itemParamItemService.saveItem(tbItemParamItem);

        //发送消息给其他系统
        sendMsg(tbItem.getId(), "insert");

        return count1.intValue() == 1 && count2.intValue() == 1 && count3.intValue() == 1;
    }


    public EasyUIResult queryItemList(Integer page, Integer rows) {
        //设置分页参数
        PageHelper.startPage(page,rows);
        //设置查询条件,按照创建时间排序
        TbItemExample example = new TbItemExample();
        example.setOrderByClause("updated DESC");
        List<TbItem> list = tbItemMapper.selectByExample(example);
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        return new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     *
     * @param item
     * @param desc
     * @return
     */
    public  Boolean updateItem(TbItem item, String desc , String itemParams){
        //强制设置不能更新的字段为null
        item.setCreated(null);
        item.setStatus(null);
        item.setUpdated(new Date());

        //更新item
        Integer count1 = tbItemMapper.updateByPrimaryKeySelective(item);

        //更新item_desc
        TbItemDesc itemDesc = new TbItemDesc() ;
        itemDesc.setItemId(item.getId());
        itemDesc.setItemDesc(desc);
        Integer count2 = itemDescMapper.updateByPrimaryKeySelective(itemDesc);

        //更新商品规格参数数据
        Integer count3 = itemParamItemService.updateItemParamItem(item.getId(), itemParams);

        //通知其他系统，该商品已经更新，通过httpclient调用前台接口
        /*try {
            String url = TAOTAO_WEB_URL + "/item/cache/" + item.getId() + ".html";
            apiService .doPost(url);
        } catch (Exception e) {
            e.printStackTrace();
        }*/


      //发送消息给其他系统
        sendMsg(item.getId(), "update");

        return count1.intValue()==1 && count2.intValue()==1 && count3.intValue()==1;
    }


    //将数据封装为json数据便于接收与解析,捕获异常，不能影响正常的业务逻辑
    public void sendMsg(Long itemId , String type){
        try {
            Map<String , Object> map = new HashMap<>();
            map.put("type", type);
            map.put("itemId" , itemId);
            map.put("date" , System.currentTimeMillis());
            rabbitTemplate.convertAndSend("item." + type, MAPPER.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public TbItem queryItemById(Long itemId) {

       /* try {
            //从缓存中命中，如果命中就返回，没有命中就继续查询，然后将结果写到缓存中
            //定义key的规则
            String key = REDIS_KEY;
            String cacheDate = this.redisService.get(key);
            if (StringUtils.isNotEmpty(cacheDate)) {
                //命中，返回
                //将数据返回为指定类型的数据
                return MAPPER.readValue(cacheDate, TbItem.class);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }*/

        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);

        if(null == item){
            return null;
        }
/*
        //将结果写到缓存中
        try {
            this.redisService.set(REDIS_KEY,MAPPER.writeValueAsString(item ),REDIS_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return item ;
    }
}
