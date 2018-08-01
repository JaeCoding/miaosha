package com.imooc.miaosha.redis;

public class AccessKey extends BasePrefix {

	private AccessKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}


	//expireSeconds  允许访问次数
	public static AccessKey withExpire(int expireSeconds) {

		return new AccessKey(expireSeconds, "access");
	}
	
}
