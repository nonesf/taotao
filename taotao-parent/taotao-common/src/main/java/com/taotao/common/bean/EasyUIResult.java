package com.taotao.common.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class EasyUIResult {

    //定义jackson对象，实现json数据与java对象的转换
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Integer total;
    private List<?> rows;

    public EasyUIResult() {
    }

    public EasyUIResult(Integer total, List<?> rows) {
        this.total = total;
        this.rows = rows;
    }

    public EasyUIResult(Long total, List<?> rows) {
        this.total = total.intValue();
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    /**
     * 将json数据转换成对象
     *
     * object 是集合转化
     * @param jsonData  json数据
     * @param clazz  集合中的类型
     * @return
     */
    public static EasyUIResult formatToList(String jsonData , Class<?> clazz){
        try {

            //json字符串转换程jsonNode对象
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("rows");
            List<?> list = null;
            if(data.isArray() && data.size() > 0){
                list = MAPPER.readValue(data.traverse() ,
                        MAPPER.getTypeFactory().constructCollectionType(List.class , clazz));
            }
            return new EasyUIResult(jsonNode.get("total").intValue() , list);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
