package com.taotao.order.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taotao.order.bean.TaotaoResult;
import com.taotao.order.pojo.Order;
import com.taotao.order.pojo.PageResult;
import com.taotao.order.pojo.ResultMsg;
import com.taotao.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("order")

public class OrderController {

    @Autowired
    private OrderService orderService ;

    /**
     * 创建订单
     * @param json  提交的json数据
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult createOrder(@RequestBody String json){
        return orderService.createOrder(json);
    }

    /**
     * 根据订单id查询订单
     * @param orderId
     * @return
     */
    @RequestMapping(value = "query/{orderId}", method = RequestMethod.GET )
    @ResponseBody
    public Order queryOrderById(@PathVariable("orderId") String orderId){
        return this.orderService.queryOrderById(orderId);
    }

    /**
     * 根据用户分页查询订单
     * @param buyerNick
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "query/{buyerNick}/{page}/{count}",method = RequestMethod.GET)
    @ResponseBody
    public PageResult<Order> queryOrderByUserNameAndPage(@PathVariable("buyerNick") String buyerNick,
                                                         @PathVariable("page") Integer page,
                                                         @PathVariable("count") Integer count){
        return this.orderService.queryOrderByUserNameAndPage(buyerNick, page, count);
    }


    @RequestMapping(value = "changeOrderStatus",method = RequestMethod.POST)
    @ResponseBody
    public ResultMsg changeOrderStatus(@RequestBody String json){
        return this.orderService.changeOrderStatus(json);
    }

  /*  public static void main(String[] args){
        System.out.println(System.currentTimeMillis());
    }*/
}
