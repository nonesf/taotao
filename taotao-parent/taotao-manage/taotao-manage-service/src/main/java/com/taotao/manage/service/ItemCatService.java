
package com.taotao.manage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.ItemCatData;
import com.taotao.common.bean.ItemCatResult;
import com.taotao.common.service.RedisService;
import com.taotao.manage.mapper.TbItemCatMapper;
import com.taotao.manage.pojo.TbItemCat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemCatService {
    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private RedisService redisService ;

    private static final ObjectMapper MAPPER = new ObjectMapper() ;

    /*优化：1、将REDIS_KEY，REDIS_TIME 作为属性，以后只需计算一次，提高性能，在程序中每次都需要计算。
    //2、将缓存逻辑放在try catch块中，因为缓存逻辑不能影响正常的业务逻辑，
    所以当出现异常的时候进行捕获就能继续业务逻辑的执行，而且使用exception，可以捕获所有的异常
    */
    private String REDIS_KEY = "TAOTAO_MANAGE_ITEM_CAT_ALL ";

    private Integer REDIS_TIME = 60*60*24*30;

    //根据父类目id获取类目信息
    public List<TbItemCat> queryItemCat(Long parentId){
        return this.tbItemCatMapper.selectByParentId(parentId);
    }

    /**
     * 查询全部的类目信息，并生成树状结构
     * @return
     */
    public ItemCatResult queryAllToTree(){
        ItemCatResult itemCatResult = new ItemCatResult();
        try {
            //从缓存中命中，如果命中就返回，没有命中就继续查询，然后将结果写到缓存中
            //定义key的规则
            String key = REDIS_KEY;
            String cacheDate = this.redisService.get(key);
            if (StringUtils.isNotEmpty(cacheDate)) {
                //命中，返回
                //将数据返回为指定类型的数据
                return MAPPER.readValue(cacheDate, ItemCatResult.class);
            }
        }catch (Exception e) {
                e.printStackTrace();
            }


        //全部查出，并且在内存中形成树形结构
        List<TbItemCat> cats = tbItemCatMapper.selectAll();

        //转为map存储，key:parent_id ,value：数据集合
        Map<Long , List<TbItemCat>> itemCatMap = new HashMap<Long  , List<TbItemCat>>();
        for(TbItemCat itemCat : cats){
            //如果map中不包含此类目的parent_id,说明还没有进行存储,存储
            if(!itemCatMap.containsKey(itemCat.getParentId())){
                itemCatMap.put(itemCat.getParentId() , new ArrayList<TbItemCat>());
            }
            //类目中包含此类目的parent_id ,将该类目加入list中
            itemCatMap.get(itemCat.getParentId()).add(itemCat);
        }

        //封装一级类目,一级类目的parent_id = 0
        List<TbItemCat> itemCatList1 = itemCatMap.get(0L);
        for(TbItemCat itemCat : itemCatList1){
            ItemCatData itemCatData = new ItemCatData();
            itemCatData.setUrl("/produces/" + itemCat.getId() + ".html");
            itemCatData.setName("<a href = '" + itemCatData.getUrl() + "'>" + itemCat.getName() + "</a>");
            //把数据添加到itemCats集合中
            itemCatResult.getItemCats().add(itemCatData);
            //判断是否为父节点
            if(!itemCat.getIsParent()){
                continue;
            }

            //封装二级类目,一级类目的id为二级类目的parent_id
            List<TbItemCat> itemCatList2 = itemCatMap.get(itemCat.getId());
            //itemCatData2为二级目录
            List<ItemCatData> itemCatData2 = new ArrayList<ItemCatData>();
            //itemCatData为一级目录，其items存放二级目录
            itemCatData.setItems(itemCatList2);
            for(TbItemCat itemCat2 : itemCatList2){
                ItemCatData id2 = new ItemCatData();
                id2.setUrl("/produces/" + itemCat2.getId() + ".html");
                id2.setName(itemCat2.getName());
                itemCatData2.add(id2);

                //该类目为类目，故封装其三级类目
                if(itemCat2.getIsParent()){

                    //封装三级类目
                    List<TbItemCat> itemCatList3 = itemCatMap.get(itemCat2.getId());
                    List<String> itemCatData3 = new ArrayList<String>();
                    id2.setItems(itemCatList3);
                    for(TbItemCat itemCat3 : itemCatList3){
                        itemCatData3.add("/produces/" + itemCat3.getId() + ".html" + itemCat3.getName());
                    }
                }
            }
            if(itemCatResult.getItemCats().size()>=14){
                break;
            }
        }

        //将结果写到缓存中
        try {
            this.redisService.set(REDIS_KEY,MAPPER.writeValueAsString(itemCatResult),REDIS_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemCatResult ;
    }

}
