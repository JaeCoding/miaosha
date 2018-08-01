package com.imooc.miaosha.result;

public class Result<T> {

    private int code;
    private String msg;
    private T data; //data是什么类型还不知道，所以定义为泛型类型

    /**
     *  成功时候的调用,通过success方法生成Result对象
     *  第一个标志这个方法是 泛型方法，第二个是List<T>是返回值。泛型方法返回值前必须带一个<T>，
     *  这是一种约定，表示该方法是泛型方法，否则报错。
     * */
    public static  <T> Result<T> success(T data){
        return new Result<T>(data);
    }


    /**
     *  失败时候的调用，
     *  通过error方法生成Result对象
     * */
    public static  <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }

    //成功时候的构造，用一个参数data构造
    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    //失败时候的构造，用code和msg构造
    private Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    //失败时候的构造，根据CodeMsg
    private Result(CodeMsg codeMsg) {
        if(codeMsg != null) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }





    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
}
