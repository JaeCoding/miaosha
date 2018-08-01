package com.imooc.miaosha.util;


import java.util.UUID;

public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");//原生的uuid是带-的，我们希望去掉
    }
}
