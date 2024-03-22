package com.edge.agent.core.manager.buffer;

import com.edge.agent.core.channel.Contact;
import lombok.Getter;

/**
 * @author zyq
 */
@Getter
public class StatusInfo {
    private final Contact contact;
    private final Boolean status;

    public StatusInfo(Contact contact, Boolean status) {
        this.contact = contact;
        this.status = status;
    }

}
