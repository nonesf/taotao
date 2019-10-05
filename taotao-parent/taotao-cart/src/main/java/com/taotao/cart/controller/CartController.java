package com.taotao.cart.controller;

import com.taotao.cart.bean.TbUser;
import com.taotao.cart.pojo.TbCart;
import com.taotao.cart.service.CartCookieService;
import com.taotao.cart.service.CartService;
import com.taotao.cart.threaLocal.UserThreadLocal;
import com.taotao.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartService cartService ;
    @Autowired
    private CartCookieService cartCookieService;

    /**
     * 查询购物车列表
     *
     * 在登录操作的时候就要合并cookie和本地购物车，
     * 查询购物车列表是一个必经的操作，所以可以在查询购物车列表的时候将其合并
     *
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ModelAndView showCartList(HttpServletRequest request,HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView("cart");

        List<TbCart> cartList= null;
        TbUser user = UserThreadLocal.get();

        List<TbCart>cartCookietList = cartCookieService.queryCartList(request);

        if(null == user){
            //未登录,在cookie中查询数据,查询数据需要request，写数据需要response
            cartList = cartCookietList;
        }else {
            //已登录,查询购物车列表
            /**
             * 判断cookie中是否有数据，有数据才进行合并，
             * 避免重复的没有必要的操作，因为每次刷新页面都会查询购物车列表
             */
            if(cartCookietList .size() >0){
                //合并购物车,将cookie数据持久化到数据库
                cartService.magerCartList(cartCookietList);
                //删除cookie中的数据，不然下次又会重复合并
                CookieUtils.deleteCookie(request,response,cartCookieService.COOKIE_NAME);
            }
            cartList = cartService.queryCartList();
        }
        modelAndView.addObject("cartList",cartList);
        return modelAndView;
    }

    /**
     * 加入商品到购物车
     *
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{itemId}/{num}" ,method = RequestMethod.GET)
    public String addItemToCart(@PathVariable("itemId") Long itemId,@PathVariable("num") Integer num,
                                HttpServletRequest request, HttpServletResponse response){
        TbUser user = UserThreadLocal.get();

        if(null == user){
            //未登录
            cartCookieService.addItemToCart(itemId, request, response);

        }else {
            //已登录,将商品加入购物车
            cartService.addItemToCart(itemId ,num);
        }

        //返回购物车列表页面
        return "redirect:/cart/list.html";
    }


    /**
     * 修改购买商品的数量
     *
     * @param itemId
     * @param num  最终购买的数量
     * @return
     */
    @RequestMapping(value = "update/num/{itemId}/{num}", method = RequestMethod.POST)
    public ResponseEntity<Void> updateNum(@PathVariable("itemId")Long itemId, @PathVariable("num")Integer num,
                                          HttpServletRequest request, HttpServletResponse response){
        TbUser user = UserThreadLocal.get();
        if(null == user){
            //未登录
            cartCookieService.updateNum(request, response ,itemId ,num);
        }else{
            //已登录
            cartService.updateNum(itemId, num);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * 删除购物车商品
     *
     * @return
     */
    @RequestMapping(value = "delete/{itemId}",method = RequestMethod.GET)
    public String deleteItem(@PathVariable("itemId") Long itemId,
                             HttpServletRequest request, HttpServletResponse response){
        TbUser user = UserThreadLocal.get();
        if(null == user){
            //未登录
            cartCookieService.deleteItem(request, response ,itemId);
        }else {
            //已登录
            cartService.deleteItem(itemId);
        }
        return "redirect:/cart/list.html";
    }

}
