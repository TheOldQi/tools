package com.xiafei.tools.akka;

import akka.actor.*;
import akka.japi.Creator;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

import java.util.concurrent.TimeUnit;

/**
 * 第一个Actor.
 */
public class ActorHello extends AbstractActor {

    private String field1;
    private Integer field2;

    /**
     * Actor实例获取方法.
     *
     * @param field1 字段1
     * @param field2 字段2
     * @return akka用的
     */
    public static Props props(String field1, Integer field2) {
        return Props.create(ActorHello.class, (Creator<ActorHello>) () -> new ActorHello(field1, field2));
    }

    private ActorHello(String field1, Integer field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                // match可以多个，匹配收到消息类型
                        match(ActorHelloMessage.class, actorHelloMessage -> {
                    System.out.println("This is ActorHello with field1:" + field1 + ",field2:" + field2
                            + ".Hello id:" + actorHelloMessage.id + ",name:" + actorHelloMessage.name);
                    // 判断是否nosender
                    if (!DeadLetterActorRef.class.isAssignableFrom(getSender().getClass())) {
                        getSender().tell("我收到啦", ActorRef.noSender());
                    }
                }).
                // 通常最后跟一个matchAny，相当于if-else结构的else
                        matchAny(obj -> {
                    System.out.println("不认识的消息类型");
                }).build();
    }

    public static void main(String[] args) throws Exception {
        // 随便取一个名字，actorSystem是顶级节点
        ActorSystem actorSystem = ActorSystem.create("helloActorSystem");
        // 获得actor操作对象
        ActorRef actorRoot = actorSystem.actorOf(ActorHello.props("0", 0), "actorRoot");
        // 向actor发送消息，随后actor的createReceive会收到消息
        // 第一个参数是消息体，第二个参数告诉actor消息是由哪个actor发出的，在回复的时候比较方便
        actorRoot.tell(new ActorHelloMessage(0L, "0号消息"), ActorRef.noSender());
        // 发送这条消息会命中matchAny逻辑
        actorRoot.tell("我是捣乱的", ActorRef.noSender());

        // 这是同步调用的办法
        Timeout timeout = new Timeout(scala.concurrent.duration.Duration.create(10, TimeUnit.SECONDS));
        Future<Object> future = Patterns.ask(actorRoot, new ActorHelloMessage(1L, "1号消息"), timeout);
        Object result1 = Await.result(future, timeout.duration());
        System.out.println("rootActor回应:" + result1);

        actorSystem.terminate();// 停止akka
    }
}
