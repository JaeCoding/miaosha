package com.imooc.miaosha.util;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    //正则表达式判断手机号，1开头后面10个数字。 正则表达式
    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String src) {
        if (StringUtils.isEmpty(src)) {
            return false;
        }
        Matcher m = mobile_pattern.matcher(src); //匹配对象 = 正则式.matcher(String)
        return m.matches();
    }

}
