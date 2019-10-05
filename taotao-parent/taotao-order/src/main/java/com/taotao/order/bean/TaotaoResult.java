package com.taotao.order.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * 淘淘商城自定义响应结构
 */
public class TaotaoResult {
    //定义Jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper() ;

    //响应业务状态
    private Integer status;

    //响应消息
    private String msg;

    //响应中的数据
    private Object data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public TaotaoResult() {
    }

    public TaotaoResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public TaotaoResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public boolean isOk(){
        return this.status == 200 ;
    }

    public static TaotaoResult build(Integer status, String msg, Object data) {
        return new TaotaoResult(status, msg, data);
    }

    public static TaotaoResult ok(Object data) {
        return new TaotaoResult(data);
    }

    public static TaotaoResult ok() {
        return new TaotaoResult(null);
    }

    public static TaotaoResult build(Integer status, String msg) {
        return new TaotaoResult(status, msg, null);
    }

    /**
     * 将json结果转化为TaotaoResult对象
     * @param jsonData json数据
     * @param clazz TaotaoResult中的object类型
     * @return
     */
    public static TaotaoResult formatToPojo(String jsonData, Class<?> clazz){
        try {
            if(null == clazz ){
                return MAPPER .readValue(jsonData, TaotaoResult.class);
            }

            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object object = null;
            if(clazz != null){
                if(data.isObject()){
                    object = MAPPER.readValue(data.traverse(), clazz);
                }else if(data .isTextual()){
                    object = MAPPER.readValue(data.asText(), clazz);
                }
            }
            return build(jsonNode.get("status").intValue(),jsonNode.get("msg").asText(),object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 没有object对象的转化
     * @param jsaonData json数据
     * @return
     */
    public static TaotaoResult format(String jsaonData){
        try {
            return MAPPER .readValue(jsaonData , TaotaoResult .class );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }

    /**
     * object 是集合转化
     * @param jsonData  json数据
     * @param clazz  集合中的类型
     * @return
     */
    public static TaotaoResult formatToList(String jsonData, Class<?> clazz){
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object object = null;
            if(data.isArray() && data.size()>0){
                object = MAPPER .readValue(data.traverse(), MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return build(jsonNode.get("status").intValue(),jsonNode.get("msg").asText(),object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
