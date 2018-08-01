package com.imooc.miaosha.redis;

public class GoodsKey extends BasePrefix{


    public GoodsKey(int expireSeconds, String prefix) {
        super(prefix);
    }



    //构造方法,设置过期时间为60s，太长的话即时性不准，太短的话又重复缓存，起不到缓存的作用
    public static GoodsKey getGoodsDetail = new GoodsKey(60,"gd");
    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0,"gs");



}
