package com.taotao.cart.handlerIntercepetor;

import com.taotao.cart.bean.TbUser;
import com.taotao.cart.service.UserService;
import com.taotao.cart.threaLocal.UserThreadLocal;
import com.taotao.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserLoginHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    private static final String COOKIE_NAME = "TT_TOKEN";

    //在用户加入购物车的时候判断用户是否登录
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //根据request和cookieName查询token(token相当于sessionID)
        String token = CookieUtils.getCookieValue(request, COOKIE_NAME);

        if(StringUtils.isEmpty(token)){
            //未登录,放行，可以加入购物车只是将商品信息存储在cookie中
            return true ;
        }

        TbUser user = userService.queryByToken(token);
        if(null == user){
            //登录超时，放行
            return true;
        }

        //登录成功,将user对象绑定到当前线程
        UserThreadLocal.set(user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //在使用之前清空，也可在使用之后就清空。（改进）
        UserThreadLocal.set(null);
    }
}
