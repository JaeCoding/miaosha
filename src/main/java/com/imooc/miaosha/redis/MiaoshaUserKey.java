package com.imooc.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix{



    public static final int TOKEN_EXPIRE = 3600 * 24 * 2; //过期时间：两天

    private MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    //token是暴露的构造方法，用内部的TOKEN_EXPIRE构造
    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"tk");

    //对象缓存，希望不要过期
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0,"id");

}
