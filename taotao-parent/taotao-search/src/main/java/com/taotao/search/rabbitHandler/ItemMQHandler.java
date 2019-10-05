package com.taotao.search.rabbitHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.search.pojo.TbItem;
import com.taotao.search.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 根据商品id 处理消息,将商品数据同步到solr中，
 * 消息中并没有包含商品的基本数据，需要通过id到后台选中查询
 *
 */
public class ItemMQHandler {

    @Autowired
    private HttpSolrServer httpSolrServer;
    @Autowired
    private ItemService itemService ;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void execute(String msg){
        //删除缓存中的数据
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            Long itemId = jsonNode.get("itemId").asLong();
            String type = jsonNode.get("type").asText();
            if(StringUtils.equals(type , "insert") || StringUtils.equals(type , "update")){
                TbItem item = itemService.queryItemById(itemId);
                httpSolrServer.addBean(item);
            }else if(StringUtils.equals(type , "delete")){
                httpSolrServer.deleteById(itemId.toString());
            }
            httpSolrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
