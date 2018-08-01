package com.imooc.miaosha.redis;

import com.imooc.miaosha.domain.User;

public class UserKey extends BasePrefix{


    //希望User永不过期，故默认传入0
    private UserKey(String prefix) {
        super(0, prefix);
    }

    //两种特殊的构造方法
    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");

}
