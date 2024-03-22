package com.edge.agent.common.result;

import java.io.Serializable;

/**
 * @author Administrator
 * @version V1.0
 * @className Result
 * @description 统一返回体
 * @date 2022/04/22 11:21
 **/
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -6405439263318521739L;

    /**
     * 状态码
     */
    private int code;
    /**
     * 返回数据
     */
    private T data;
    /**
     * 提示信息
     */
    private String msg;

    public Result<T> setResultCode(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMessage();
        return this;
    }

    public int getCode() {
        return code;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
