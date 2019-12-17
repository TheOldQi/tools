package com.xiafei.tools.akka;

import java.io.Serializable;

/**
 * Actor消息类型.
 */
public final class ActorHelloMessage implements Serializable {

    public final Long id;
    public final String name;

    public ActorHelloMessage(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
