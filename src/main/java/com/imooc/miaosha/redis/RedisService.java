package com.imooc.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;//自动装载工厂方法生成的JedisPool


    /**
     * 从redis中获取单个对象
     * @param prefix 前缀，用于和key生成realkey
     * @param key
     * @param clazz 所取出value的class
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        //在此使用jedis，用连接池获取,并且用完后要释放

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;

            String str = jedis.get(realKey);//jedis的get方法，获取到String类型，但是我们的是T类型

            T t = stringToBean(str, clazz);//字符串(类名）转 Bean对象的方法
            return t;
        }finally {
            returnToPool(jedis);
        }

    }

    /**
     * 设置对象
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix,String key, T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);//value is T type, change it to string
            if (str == null || str.length() <= 0) {
                return false;
            }

            //prefix.getPrefix()会获取到className + ":" + prefix，
            String realKey = prefix.getPrefix() + key;//realKey = param1（className：prefix（id or name）+ key（""+1）

            int seconds = prefix.expireSeconds();
            if (seconds <= 0) {
                jedis.set(realKey, str);
            }else {
                jedis.setex(realKey, seconds, str); //根据过期时间设置
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除
     * */
    public boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            long ret =  jedis.del(realKey);
            return ret > 0;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Boolean exists(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;//realKey = param1（className：prefix（id or name）+ key（""+1）
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;//realKey = param1（className：prefix（id or name）+ key（""+1）
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    public <T> Long desr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;//realKey = param1（className：prefix（id or name）+ key（""+1）
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }






    public static  <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() == 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class ){
            return (T)Integer.valueOf(str);
        } else if (clazz == String.class){
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);//传入JSON和CLASS，返回clazz对应的Bean
        }
    }


    public static  <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class || clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else if (clazz == String.class){
            return (String) value;
        } else {
            return JSON.toJSONString(value);//toJSONString can not alter the int and String??
        }
    }


    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();//查询后得知，关闭其实是return到连接池
        }
    }



}
