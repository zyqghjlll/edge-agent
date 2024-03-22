package com.edge.agent.core.manager;

import com.edge.agent.core.channel.Contact;
import lombok.Getter;

import java.util.Date;

/**
 * @author zyq
 */
@Getter
public class RawInfo<T> {
    private final Long pkRecord;
    private final Contact contact;
    private final T rawData;
    private final Date receiveTime;

    public RawInfo(Long pkRecord, Contact contact, T rawData, Date receiveTime) {
        this.pkRecord = pkRecord;
        this.contact = contact;
        this.rawData = rawData;
        this.receiveTime = receiveTime;
    }

    @Override
    public String toString() {
        return "RawInfo{" +
                "pkRecord=" + pkRecord +
                ", contact=" + contact +
                ", rawData=" + rawData +
                ", receiveTime=" + receiveTime +
                '}';
    }
}
