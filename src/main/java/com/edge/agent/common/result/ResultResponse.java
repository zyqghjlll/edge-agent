package com.edge.agent.common.result;

/**
 * @author Administrator
 * @version V1.0
 * @className ResultResponse
 * @description
 * @date 2022/04/22 11:27
 **/
public class ResultResponse {

    private ResultResponse() {
    }

    /**
     * 响应成功，200，无数据返回
     *
     * @return
     */
    public static Result success() {
        return new Result().setResultCode(ResultCode.SUCCESS);
    }

    /**
     * 响应成功，200，返回数据
     *
     * @param data 返回数据
     * @param <T>  返回类型泛型
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result().setResultCode(ResultCode.SUCCESS).setData(data);
    }

    /**
     * 返回失败 400
     *
     * @return
     */
    public static Result fail(String message) {
        Result result = new Result();
        result.setResultCode(ResultCode.FAIL);
        result.setMsg(message);
        return result;
    }

    /**
     * 系统内部错误 500
     *
     * @return
     */
    public static Result serverError() {
        return new Result().setResultCode(ResultCode.INTERNAL_SERVER_ERROR);
    }
}
