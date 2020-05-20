package com.eina.chat.backendapi;

import com.eina.chat.backendapi.controller.CommandAPIController;
import com.eina.chat.backendapi.protocol.packages.BasicPackage;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationFailResponse;
import com.eina.chat.backendapi.protocol.packages.common.response.OperationSucceedResponse;
import com.eina.chat.backendapi.protocol.packages.message.request.SendFileToRoomCommand;
import com.eina.chat.backendapi.protocol.packages.message.request.SendFileToUserCommand;
import com.eina.chat.backendapi.protocol.packages.message.request.SendMessageToRoomCommand;
import com.eina.chat.backendapi.protocol.packages.message.request.SendMessageToUserCommand;
import com.eina.chat.backendapi.protocol.packages.message.response.MessageFromUserResponse;
import com.eina.chat.backendapi.security.AccessLevels;
import com.eina.chat.backendapi.service.MessageBrokerAPI;
import com.eina.chat.backendapi.service.PersistentDataAPI;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.aspectj.bridge.MessageUtil.fail;

@SpringBootApplication
public class BackEndApiApplication {

//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//        return args -> {
//            System.out.println("HOLAAAAAAa");
//            System.out.println("PORT: " + System.getenv("PORT"));
//        };
//    }

    public static void main(String[] args) {
        SpringApplication.run(BackEndApiApplication.class, args);
    }
}
