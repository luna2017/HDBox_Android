package com.ibookpa.hdbox.android.model;

/**
 * Created by tc on 6/21/16.网络请求返回结果
 */
public class ResponseModel<T> {
    private int code;
    private String message;

    private T result;

    public ResponseModel(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
