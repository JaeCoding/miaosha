package com.imooc.miaosha.util;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    private static final String salt = "1a2b3c4d"; //set a fixed salt


    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    //第一次MD5，固定的salt
    public static String inputPassFormPass(String inputPass) {
        String string = ""+salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(string);
    }

    //第二次md5，在服务端可以用随机salt
    public static String formPassToDBPass(String formPass, String salt) {
        String string = ""+salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(string);
    }

    public static String inputPassToDBPass(String input, String saltDB) {
        return formPassToDBPass(inputPassFormPass(input), saltDB);
    }

    public static void main(String[] args) {
        System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));// 12 123456 c3反查彩虹表的结果
    }



}
