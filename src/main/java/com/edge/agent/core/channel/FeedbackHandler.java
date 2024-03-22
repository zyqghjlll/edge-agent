package com.edge.agent.core.channel;

import cn.hutool.core.util.ObjectUtil;
import com.edge.agent.core.channel.exception.FeedbackException;
import com.edge.agent.core.netpoint.NetPoint;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zyq
 */
public abstract class FeedbackHandler<U, D> {
    private String identify;
    private NetPoint local;
    private NetPoint remote;
    private FeedbackStatus status;
    private Timer timer;

    protected FeedbackHandler(String identify, NetPoint local, NetPoint remote) {
        this.identify = identify;
        this.local = local;
        this.remote = remote;
        this.status = FeedbackStatus.FEEDBACK_UNSTART;
    }

    public String getIdentify() {
        return this.identify;
    }

    public NetPoint getLocal() {
        return this.local;
    }

    public NetPoint getRemote() {
        return this.remote;
    }

    /**
     * 状态
     *
     * @return
     */
    protected FeedbackStatus status() {
        return this.status;
    }

    /**
     * 是否已完成
     *
     * @return
     */
    public boolean isCompleted() {
        boolean result = false;
        if (!this.status.equals(FeedbackStatus.FEEDBACK_PROCESSING)) {
            result = true;
        }
        return result;
    }

    /**
     * 发送成功
     *
     * @return
     */
    protected void sendSucceed() {
        timerRun();
    }

    /**
     * 发送失败
     *
     * @return
     */
    protected void sendFailed(String message) {
        this.status = FeedbackStatus.FEEDBACK_EXCEPTION;
        failed(new FeedbackException(message));
    }

    /**
     * 超时时间
     *
     * @return
     */
    protected abstract Long timeout();

    /**
     * 需要发送的数据
     *
     * @return
     */
    protected abstract D sendData() throws FeedbackException;

    /**
     * feedback 过滤器
     *
     * @return
     */
    protected abstract boolean filter(U data) throws FeedbackException;

    /**
     * 监听数据
     *
     * @param data
     */
    protected void listener(U data) {
        if (isCompleted()) {
            return;
        }
        boolean filter = false;
        try {
            filter = this.filter(data);
        } catch (FeedbackException e) {
            failed(new FeedbackException(e));
        }
        if (filter) {
            this.succeed(data);
        }
    }

    /**
     * 回调成功处理
     *
     * @return
     */
    protected abstract void feedbackSucceedHandler(U data);

    /**
     * 回调失败处理
     *
     * @return
     */
    protected abstract void feedbackFailedHandler(String message);


    private void timerRun() {
        this.status = FeedbackStatus.FEEDBACK_PROCESSING;
        try {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    status = FeedbackStatus.FEEDBACK_TIMEOUT;
                    failed(new FeedbackException("Feedback 等待相应超时"));
                }
            };
            // 延迟 timeout毫秒后执行任务
            this.timer = new Timer();
            this.timer.schedule(task, timeout());
        } catch (Exception ex) {
            failed(new FeedbackException(ex));
        }
    }

    private void succeed(U data) {
        cancelTimer();
        this.status = FeedbackStatus.FEEDBACK_SUCCEEDED;
        this.feedbackSucceedHandler(data);
    }

    private void failed(FeedbackException ex) {
        cancelTimer();
        this.status = FeedbackStatus.FEEDBACK_EXCEPTION;
        feedbackFailedHandler(String.format("Feedback 异常，错误信息：[%s]-[%s]", ex.getMessage(), ex.getCause()));
    }

    private void cancelTimer() {
        if (ObjectUtil.isNotEmpty(timer)) {
            timer.cancel();
        }
    }
}
