package com.rhino.ui.demo.http.response;

/**
 * @author LuoLin
 * @since Create on 2021/6/5
 **/
public class BaseResponse<T> {

    public String message;
    public int code;
    public T data;

}
