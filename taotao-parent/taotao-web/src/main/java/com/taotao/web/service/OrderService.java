package com.taotao.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.httpclient.HttpResult;
import com.taotao.common.service.ApiService;
import com.taotao.web.pojo.Order;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OrderService {

    @Autowired
    private ApiService apiService;

    @Value("${TAOTAO_ORDER_URL}")
    public String TAOTAO_ORDER_URL;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 把订单提交到订单系统
     *
     * @param order
     * @return
     */
    public String submitOrder(Order order) {
        String url = TAOTAO_ORDER_URL + "/order/create";
        try {
            HttpResult  httpResult = apiService .doPostJson(url, MAPPER.writeValueAsString(order));
            if(httpResult.getCode() == 200){
                //post方法执行成功,获取响应内容
                String jsonData = httpResult.getBody();
                JsonNode jsonNode = MAPPER.readTree(jsonData);

                if(jsonNode.get("status").intValue() == 200){
                    //业务操作方法执行成功,将data内容即订单id返回
                    return jsonNode.get("data").asText();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Order queryOrderById(String orderId) {
        String url = TAOTAO_ORDER_URL + "/order/query/" + orderId;
        try {
            String jsonData = apiService.doGet(url);
            if(StringUtils.isNotEmpty(jsonData)){
                return MAPPER.readValue(jsonData, Order.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
