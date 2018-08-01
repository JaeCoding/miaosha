package com.imooc.miaosha.redis;

public class MiaoshaKey extends BasePrefix{


    public MiaoshaKey(int expireSeconds, String prefix) {
        super(prefix);
    }



    //构造方法,设置过期时间为60s，太长的话即时性不准，太短的话又重复缓存，起不到缓存的作用
    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "go");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "vc");


}
