package com.edge.agent.common.result;

/**
 * @author zyq
 * @date 2022/10/21 11:40
 */
public interface IPromptMessage {
    /**
     * 获取Code
     * @return
     */
    int getCode();

    /**
     * 获取Value
     * @return
     */
    String getMessage();
}
