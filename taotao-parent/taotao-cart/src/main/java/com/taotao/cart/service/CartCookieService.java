package com.taotao.cart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.cart.bean.TbItem;
import com.taotao.cart.pojo.TbCart;
import com.taotao.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class CartCookieService {

    public static final String COOKIE_NAME = "TT_CART";

    private static final ObjectMapper MAPPER = new ObjectMapper() ;

    private static final Integer COOKIE_TIME = 60 * 60 * 24 * 30 * 12 ;

    @Autowired
    private ItemService itemService;

    /**
     * 查询商品数据
     * 从cookie中获取数据进行判断是否为空，不为空则返回数据
     *
     * @param request
     * @return
     */
    public List<TbCart> queryCartList(HttpServletRequest request) {
        //isDecoder：true 解码，因为在存储的时候进行了编码
        //cookie中存储的是字符串数据
        String cookieValue = CookieUtils.getCookieValue(request,COOKIE_NAME,true);

        List<TbCart> cartList = null;
        if(StringUtils .isNotEmpty(cookieValue)){
            //cookie中存在数据,反序列化后返回,因为反序列化后的数据类型为List<TbCart>，所以采用如下方法
            try {
                cartList = MAPPER.readValue(cookieValue, MAPPER.getTypeFactory().constructCollectionType(List.class , TbCart .class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            //不存在,新建一个集合用于存放数据
            cartList = new ArrayList<TbCart>();
        }
        return cartList ;
    }


    /**
     * 首先从cookie中获取数据，判断是否存在，如果存在数量相加，否则直接添加
     *
     * @param itemId
     * @param request
     * @param response
     */
    public void addItemToCart(Long itemId, HttpServletRequest request, HttpServletResponse response) {
        List<TbCart> cartList = queryCartList(request);

        //遍历获取的数据，判断是否存在要添加的数据
        TbCart carts = null;

        for (TbCart cart : cartList) {
            if(cart.getItemId().longValue() == itemId.longValue()){
                //数据存在
                carts = cart;
                break;
            }
        }
        if(null == carts){
            //数据不存在,直接添加
            carts.setItemId(itemId);
            carts.setCreated(new Date());
            carts.setUpdated(carts.getCreated());

            //从后台系统查询商品的基本数据
            TbItem item = itemService.queryItemById(itemId);
            carts.setItemTitle(item.getTitle());
            carts.setItemPrice(item.getPrice());
            carts.setItemImage(StringUtils.split(item.getImage(),",")[0]);
            carts.setNum(1);

            //商品加入购物车列表
            cartList.add(carts);
        }else {
            //数据存在
            carts.setNum(carts.getNum() + 1);
            carts.setUpdated(new Date());
        }
        //将商品写入cookie中
        try {
            CookieUtils.setCookie(request ,response, COOKIE_NAME, MAPPER.writeValueAsString(cartList), COOKIE_TIME, true );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void updateNum(HttpServletRequest request, HttpServletResponse response, Long itemId, Integer num) {
        List<TbCart> cartList = queryCartList(request);
        TbCart carts = null;
        for(TbCart cart : cartList){
            if(cart.getItemId().longValue() == itemId.longValue()){
                carts = cart ;
                break;
            }
        }

        if(null != carts){
            carts.setNum(num);
            carts.setUpdated(new Date());
        }else{
            //数据不存在，参数非法,在此可以直接返回，如果没有这条语句就会多执行一次写入cookie的操作，增加代码的健壮性
            return;
        }

        //将数据写入cookie中
        try {
            CookieUtils.setCookie(request, response,COOKIE_NAME, MAPPER.writeValueAsString(cartList),COOKIE_TIME,true );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(HttpServletRequest request, HttpServletResponse response, Long itemId) {
        List<TbCart> cartList = queryCartList(request);
        TbCart carts = null ;
        for(TbCart cart : cartList){
            if(cart.getItemId().longValue() == itemId.longValue()){
                carts = cart;
                break;
            }
        }

        //判断cookie中是否存在数据
        if(carts != null ){
            //存在数据,删除
            cartList.remove(carts);
        }else {
            //参数非法
            return ;
        }
        //将数据写入cookie中
        try {
            CookieUtils.setCookie(request, response,COOKIE_NAME, MAPPER.writeValueAsString(cartList),COOKIE_TIME,true );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
