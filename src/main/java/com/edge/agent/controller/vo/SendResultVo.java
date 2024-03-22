package com.edge.agent.controller.vo;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author zyq
 */
@Getter
public class SendResultVo implements Serializable {
    private String assetId;
    private String code;
    private String message;

    public SendResultVo(String assetId) {
        this.assetId = assetId;
        this.code = "-1";
        this.message = "未发送";
    }

    public void succeed() {
        this.code = "1";
        this.message = "发送成功";
    }

    public void mockSucceed() {
        this.code = "2";
        this.message = "模拟发送成功";
    }

    public void failed() {
        this.failed("");
    }

    public void failed(String message) {
        this.code = "0";
        this.message = "发送失败:" + message;
    }
}
