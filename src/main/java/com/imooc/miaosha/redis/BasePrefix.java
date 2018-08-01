package com.imooc.miaosha.redis;

public abstract class BasePrefix implements KeyPrefix {


    private int expireSeconds;//过期时间

    private String prefix;


    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }
    public BasePrefix(String prefix) {
        this(0, prefix);
    }


    @Override
    public int expireSeconds() {// 默认0代表用不过期
        return expireSeconds;
    }

    /**
     *
     * @return 字符串形式的名称 如 "gs"
     */
    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;//构造函数传入的参数，单参数时候是id，或者name
    }
}
