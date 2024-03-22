package com.edge.agent.common.result;

/**
 * @author Administrator
 * @version V1.0
 * @enumName ResultCode
 * @description 响应码枚举，参考HTTP状态码的语义
 * @date 2022/04/22 11:18
 **/
public enum ResultCode implements IPromptMessage {
    // 成功
    SUCCESS(200, "success"),
    // 失败
    FAIL(400, "fail"),
    NOT_FOUND(404, "接口未找到"),
    // 服务器内部错误
    INTERNAL_SERVER_ERROR(500, "internal error"),

    ;

    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
