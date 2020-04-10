package com.eina.chat.backendapi.rabbitmq;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConsumersContainer {
    /**
     * used to store global consumers and queues
     */
    private final Map<String, SimpleMessageListenerContainer> consumers;

    public ConsumersContainer(){
        consumers = new ConcurrentHashMap<>();
    }

    public boolean addConsumer(String username, SimpleMessageListenerContainer listener){
        if (!consumers.containsKey(username)){
            consumers.put(username, listener);
            return true;
        }
        return false;
    }

    public SimpleMessageListenerContainer getConsumer(String username) {
        return consumers.get(username);
    }

    public boolean deleteConsumer(String username){

        return consumers.remove(username)!=null;
    }
}