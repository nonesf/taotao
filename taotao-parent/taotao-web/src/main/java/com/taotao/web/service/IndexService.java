package com.taotao.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.common.service.ApiService;
import com.taotao.manage.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexService {

    @Autowired
    private ApiService apiService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${TAOTAO_MANAGE_URL}")
    private String TAOTAO_MANAGE_URL;

    @Value("${INDEX_AD1_URL}")
    private String INDEX_AD1_URL;

    @Value("${INDEX_AD2_URL}")
    private String INDEX_AD2_URL;

    public String queryIndexAD1() {
        String url = TAOTAO_MANAGE_URL + INDEX_AD2_URL;
        try {
            String jsonData = apiService.doGet(url);
            if(StringUtils.isEmpty(jsonData)){
                return null;
            }

            //将json数据转换成对象操作
            EasyUIResult easyUIResult = EasyUIResult.formatToList(jsonData , TbContent.class);
            //获取所有的对象数据
            List<TbContent> contents= (List<TbContent>) easyUIResult.getRows();
            //result用来返回最后的结果
            List<Map<String , Object>> result = new ArrayList<>();
            //遍历rows将其内容按照前端要求进行封装
            for(TbContent content : contents){
                //前端数据结构固定，且为k-v结构,linkedHashMap有序
                Map<String , Object> map = new LinkedHashMap<>();
                //asText将获取的json对象转换成文本数据
                map.put("srcB" , content.getPic());
                map.put("height" , 240);
                map.put("alt" , content.getTitle());
                map.put("width" , 670);
                map.put("src" , content.getPic2());
                map.put("widthB" , 550);
                map.put("href" , content.getUrl());
                map.put("heightB" , 240);
                result.add(map);
            }
            return MAPPER.writeValueAsString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public String queryIndexAD1() {
        String url = TAOTAO_MANAGE_URL + INDEX_AD2_URL;
        try {
            String jsonData = apiService.doGet(url);
            if(StringUtils.isEmpty(jsonData)){
                return null;
            }
            //解析json数据，封装成前端所需要的结构
            //将数据封装成树状结构
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            //将rows的内容读出来放入数组中
            ArrayNode rows = (ArrayNode) jsonNode.get("rows");
            //result用来返回最后的结果
            List<Map<String , Object>> result = new ArrayList<>();
            //遍历rows将其内容按照前端要求进行封装
            for(JsonNode row : rows){
                //前端数据结构固定，且为k-v结构,linkedHashMap有序
                Map<String , Object> map = new LinkedHashMap<>();
                //asText将获取的json对象转换成文本数据
                map.put("srcB" , row.get("pic").asText());
                map.put("height" , 240);
                map.put("alt" , row.get("title").asText());
                map.put("width" , 670);
                map.put("src" , row.get("pic").asText());
                map.put("widthB" , 550);
                map.put("href" , row.get("url").asText());
                map.put("heightB" , 240);
                result.add(map);
            }
            return MAPPER.writeValueAsString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public String queryIndexAD2() {
        try {
            String url = TAOTAO_MANAGE_URL + INDEX_AD1_URL;
            String jsonData = this.apiService.doGet(url);
            //解析数据
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            ArrayNode rows = (ArrayNode) jsonNode.get("rows");
            List<Map<String , Object>> result = new ArrayList<>();

            for(JsonNode row : rows){
                Map<String , Object> map = new LinkedHashMap<>();
                map.put("width" , 310);
                map.put("height" , 70);
                map.put("src" , row.get("pic").asText());
                map.put("href" , row.get("url").asText());
                map.put("alt" , row.get("title").asText());
                map.put("widthB" , 210);
                map.put("heightB" , 70);
                map.put("srcB" , row.get("pic").asText());
                result.add(map);
            }
            return MAPPER.writeValueAsString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
