package com.taotao.manage.datasource;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;

/**
 * 定义数据源的AOP界面，通过判断该service的方法名来确定是走读库还是写库
 */
public class DataSourceAspect {

    /**
     * 在进入service之前执行
     * @param joinPoint 切面对象
     */
    public void before(JoinPoint joinPoint ){
        //获取到当前执行的方法名
        String methodName = joinPoint.getSignature().getName();
        if(isSlave(methodName)){
            //标记为写库
            DynamicDataSourceHolder.markMaster();
        }else {
            //标记为读库
            DynamicDataSourceHolder.markSlave();
        }
    }

    /**
     * 判断是否为读库
     * @return
     */
    public boolean isSlave(String methodName){
        //方法名以query、find、get开头的都走从库
        return StringUtils.startsWithAny(methodName, "query","find","get");
    }
}
