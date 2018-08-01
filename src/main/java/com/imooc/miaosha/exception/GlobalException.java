package com.imooc.miaosha.exception;

import com.imooc.miaosha.result.CodeMsg;


/**
 * 定义一个全局异常类，这样MiaoshaUserService的login中就不用return CodeMsg.SERVER_ERROR而是直接抛出异常
 */
public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 1L; //用处暂时不明

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    private CodeMsg codeMsg;

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }

}
