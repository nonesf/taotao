package com.taotao.web.interceptors;

import com.taotao.common.utils.CookieUtils;
import com.taotao.web.pojo.TbUser;
import com.taotao.web.service.UserService;
import com.taotao.web.threaLocal.UserThreadLocal;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserLoginHandleInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    public static final String COOKIE_NAME = "TT_TOKEN";


    //在提交订单之前进行拦截,在进入controller之前会执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 清空theadLocal中的线程，因为在tomcat中，会为每一个请求都创建一个线程，但是是通过线程池实现，可提供性能，节约资源，
         * 但是线程池中的线程并不是真正的创建于销毁，只是在不用的时候放回线程池，所以，如果在此处不清空的话会出现线程错乱的问题，
         * 用到了之前未销毁但并不属于这个bean的对象，如a用到b的信息
         */
     /*   UserThreadLocal.set(null);*/

        String token = CookieUtils.getCookieValue(request,COOKIE_NAME);
        if(StringUtils.isEmpty(token)){
            //未登录，跳转到登录页面
            return true;
        }

        //判断token的内容是否为空
        TbUser user = this.userService.queryByToken(token);

        if(null == user){
            //未登录，跳转登录页面
            return true ;
        }
        //登录成功,将user对象绑定到当前线程中
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
