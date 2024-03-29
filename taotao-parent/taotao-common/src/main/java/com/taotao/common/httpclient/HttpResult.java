package com.taotao.common.httpclient;

public class HttpResult {
    //状态码
    private int code;
    //返回的内容
    private String body;

    public HttpResult() {
    }

    public HttpResult(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
