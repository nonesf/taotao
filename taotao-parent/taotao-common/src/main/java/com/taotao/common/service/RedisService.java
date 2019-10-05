package com.taotao.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;


@Service
public class RedisService{

    @Autowired(required = false)//从spring容器中找bean，如果找到就使用，没有找到将就忽略
    private ShardedJedisPool shardedJedisPool ;

    /**
     * 采用匿名内部类的方式进行抽取优化
     * @param fun
     * @param <T>返回类型不确定，采用泛型
     */
    private <T> T execute(Function<T ,ShardedJedis> fun){
        ShardedJedis shardedJedis = null;
        try {
            //从连接池获取jedis分片资源
            shardedJedis = shardedJedisPool.getResource();

            return fun.callback(shardedJedis);
        } finally {
            if(shardedJedis != null){
                //关闭，检测连接是否有效，有效则放回连接池，无效则重置
                shardedJedis.close();
            }
        }
    }

    public String set(String key, String value){
       return  this.execute(new Function<String ,ShardedJedis >(){
            @Override
            public String callback(ShardedJedis shardedJedis) {
                return shardedJedis.set(key ,value);
            }
        });
    }

    public String get(String key){
        return this.execute(new Function<String, ShardedJedis>() {
            @Override
            public String callback(ShardedJedis shardedJedis) {
                return shardedJedis.get(key);
            }
        });
    }


    public Long del(final String key){
        return this.execute(new Function<Long, ShardedJedis>() {
            @Override
            public Long callback(ShardedJedis shardedJedis) {
                return shardedJedis.del(key);
            }
        });
    }


    public Long expire(String key){
        return this.execute(new Function<Long, ShardedJedis>() {
            @Override
            public Long callback(ShardedJedis shardedJedis) {
                //时间单位为：s
                return shardedJedis.expire(key , 60*60*24*30);
            }
        });
    }

    public Long expire(String key, Integer seconds){
        return this.execute(new Function<Long, ShardedJedis>() {
            @Override
            public Long callback(ShardedJedis shardedJedis) {
                //时间单位为：s
                return shardedJedis.expire(key , seconds);
            }
        });
    }

    //设置生存时间和数据
    public String set(String key, String value, Integer seconds){
        return this.execute(new Function<String, ShardedJedis>() {
            @Override
            public String callback(ShardedJedis shardedJedis) {
                String str = shardedJedis .set(key, value);
                shardedJedis.expire(key, seconds);
                return str;
            }
        });
    }

    /*
    */

    /**
     * 执行set操作
     * @param key
     * @param value
     * @return
     *//*
    public String set(String key , String value){
        ShardedJedis shardedJedis = null;
        try {
            //从连接池获取jedis分片资源
            shardedJedis = shardedJedisPool.getResource();
            //通过jedis分片资源进行数据存取
            return shardedJedis.set(key , value);
        } finally {
            if(shardedJedis != null){
                //关闭，检测连接是否有效，有效则放回连接池，无效则重置
                shardedJedis.close();
            }
        }
    }

    *//**
     * 执行get操作
     * @param key
     * @return
     *//*
    public String get(String key){
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedJedisPool.getResource();
            return shardedJedis.get(key);
        } finally {
            if(null != shardedJedis ){
                shardedJedis.close();
            }
        }
    }

    */

}
