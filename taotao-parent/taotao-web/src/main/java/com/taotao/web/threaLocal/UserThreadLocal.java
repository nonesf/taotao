package com.taotao.web.threaLocal;

import com.taotao.web.pojo.TbUser;

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
