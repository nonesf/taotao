package com.taotao.manage.datasource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.annotation.Resources;
import java.io.InputStream;

public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 定义动态数据源，通过集成spring提供的AbstractRoutingDataSource，实现其定义的方法，
     * 由于DynamicDataSource 是单例的，线程不安全，所以采用ThreadLocal保证线程安全，由DynamicDataSourceHolder完成
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        //采用DynamicDataSourceHolder保证线程安全
        return DynamicDataSourceHolder.getDataSourceKey();
    }


    public static void main(String ars[]){
        InputStream inputStream = null;
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = factory.openSession();

    }


}
