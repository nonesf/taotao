package com.taotao.web.controller;

import com.taotao.manage.pojo.TbItem;

import com.taotao.web.pojo.Order;
import com.taotao.web.pojo.TbCart;
import com.taotao.web.pojo.TbUser;
import com.taotao.web.service.CartService;
import com.taotao.web.service.ItemService;
import com.taotao.web.service.OrderService;
import com.taotao.web.service.UserService;
import com.taotao.web.threaLocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("order")
public class OrderController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService ;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    /**
     * 订单确认页
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{itemId}",method = RequestMethod.GET)
    public ModelAndView toOrder(@PathVariable("itemId")Long itemId){
        ModelAndView modelAndView = new ModelAndView("order");
        TbItem item = itemService.queryItemById(itemId);
        modelAndView.addObject("item", item);
        return modelAndView;
    }


    /**
     * 基于购物车的订单确认页，在购物车中点击结算跳转该页面
     *
     * @return
     */
    @RequestMapping(value = "create" ,method = RequestMethod.GET)
    public ModelAndView toCartOrder(){
        ModelAndView modelAndView = new ModelAndView("order-cart-old") ;

        //通过购物车系统的接口查询购物车数据
        List<TbCart> carts = cartService.queryCartList();
        modelAndView .addObject("carts",carts);
        return modelAndView ;
    }


    /**
     * 提交订单
     *
     * @param order
     * @param token
     * @CookieValue 获取到cookie中的内容
     *
     * @return
     */
    @RequestMapping(value = "submit", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object > submitOrder(Order order
                                           /* @CookieValue(UserLoginHandleInterceptor.COOKIE_NAME) String token*/){
        Map<String, Object> result = new HashMap<>();

        //通过token查询用户信息
        /**
         * 问题：通过token进行了两次查询，一次是在拦截器中，一次是在这里。存在性能和资源浪费问题。如何通过拦截器将user对象传给controller？
         * 拦截器在controller之前执行，拦截器和controller是一个request请求，所以：
         * 1、将user对象放在request对象中
         * 2、使用ThreadLocal对象，进入tomcat和产生响应前都处于同一线程中
         */

        //查询user数据
        TbUser user = UserThreadLocal.get();

        //设置当前用户信息
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());

        order.setCreateTime(new Date());
        order.setUpdateTime(order.getCreateTime());

        String orderId = this.orderService.submitOrder(order);
        if(StringUtils.isEmpty(orderId)){
            //提交订单失败
            result.put("status",500);
        }else{
            result.put("status", 200);
            result.put("data", orderId);
        }
        return result;
    }

    @RequestMapping(value = "success",method = RequestMethod.GET)
    public ModelAndView success(@RequestParam("id") String orderId){
        ModelAndView modelAndView = new ModelAndView("success");

        //订单数据
        Order order = this.orderService.queryOrderById(orderId);
        modelAndView.addObject("order", order);
        //送货时间,预计两天之后送达
        modelAndView.addObject("date", new DateTime().plusDays(2).toString("MM月dd日"));
        return modelAndView;
    }

}
