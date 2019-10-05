package com.taotao.cart.threaLocal;

import com.taotao.cart.bean.TbUser;

public class UserThreadLocal {

    private static final ThreadLocal<TbUser> THREAD_LOCAL = new ThreadLocal<>();

    private UserThreadLocal(){}

    public static void set(TbUser user){
        THREAD_LOCAL.set(user);
    }

    public static TbUser get(){
        return THREAD_LOCAL.get();
    }
}
