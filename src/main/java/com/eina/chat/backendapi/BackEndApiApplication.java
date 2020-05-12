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

    @Autowired
    private CommandAPIController commandAPIController;

    // Database service
    @Autowired
    private PersistentDataAPI persistentDataAPI;

    // RabbitMQ API
    @Autowired
    private MessageBrokerAPI messageBrokerAPI;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            Client c1 = new Client("user1", "pass1");
            Client c2 = new Client("user2", "pass2");
            String roomName = "room1";
            c1.preProcess();
            c2.preProcess();


            // Create room
            persistentDataAPI.createGroup(c1.nameUser, roomName);
            messageBrokerAPI.addUserToGroup(c1.nameUser, roomName);
            persistentDataAPI.addUserToGroup(c2.nameUser, roomName);
            messageBrokerAPI.addUserToGroup(c2.nameUser, roomName);
            boolean finish = false;
            while(!finish) {
                Random random = new Random();
                Thread.sleep(2000);
                int tot = 0;
                for (int i = 0; i < 10; i++) {
                    int rand = 10;
//                int rand = random.nextInt(50);
                    tot += rand;
                    for (int j = 0; j < rand; j++) {
                        int len = random.nextInt(20) + 3;
                        byte[] msg = new byte[len];
                        random.nextBytes(msg);
                        BasicPackage resp;
                        if (i % 2 == 0) {
                            if (j % 2 == 0) {
                                resp = commandAPIController.handlerSendMessageToUserCommand(c1.nameUser,
                                        new SendMessageToUserCommand(j + i * 10, c2.nameUser, new String(msg)));
                            } else {
                                resp = commandAPIController.handlerSendMessageToRoomCommand(c1.nameUser, new SendMessageToRoomCommand(j + i * 10, roomName, new String(msg)));
                            }
                        } else {
                            if (j % 2 == 0) {
                                resp = commandAPIController.handlerSendFileToUserCommand(c2.nameUser, new SendFileToUserCommand(j + i * 10, c1.nameUser, msg));
                            } else {
                                resp = commandAPIController.handlerSendFileToRoomCommand(c2.nameUser, new SendFileToRoomCommand(j + i * 10, roomName, msg));
                            }
                        }
                        if (resp instanceof OperationFailResponse) {
                            System.err.println(i + j + "  " + ((OperationFailResponse) resp).getDescription());
                            break;
                        }
                    }
                }
                System.out.println("Total msg: " + tot);
                Scanner scanner = new Scanner(System.in);
                System.out.print("Repeat comm?: yes/no ");
                String userDec = scanner.nextLine();
                if (!userDec.equals("yes")){
                    finish = true;
                }
            }
            c1.postProcess();
            c2.postProcess();
            persistentDataAPI.deleteGroup(roomName);
            messageBrokerAPI.deleteGroup(roomName);        };
    }

    private class Client{
        // Variables
        final String nameUser;

        final String pass;


        private Client(String username, String pass){
            nameUser = username;
            this.pass = pass;

        }
        public void postProcess() {
            // Delete users from all databases
            persistentDataAPI.deleteUser(nameUser);

            // Delete users from broker
            messageBrokerAPI.deleteUser(nameUser);
        }

        public void preProcess() {
            // Delete users from all databases
            persistentDataAPI.deleteUser(nameUser);

            // Delete groups where are admin
            List<String> groupsWereAdminUser1 = persistentDataAPI.getAllGroupsWhereIsAdmin(nameUser);
            for (String i : groupsWereAdminUser1){
                messageBrokerAPI.deleteGroup(i);
            }

            List<String> groupsWereAdminUser2 = persistentDataAPI.getAllGroupsWhereIsAdmin(nameUser);
            for (String i : groupsWereAdminUser2){
                messageBrokerAPI.deleteGroup(i);
            }

            // Delete users from broker
            messageBrokerAPI.deleteUser(nameUser);

            // Create users in database
            persistentDataAPI.createUser(nameUser, pass, AccessLevels.ROLE_USER);

            // Create users in broker
            messageBrokerAPI.createUser(nameUser);

        }

    }

    public static void main(String[] args) {
        SpringApplication.run(BackEndApiApplication.class, args);
    }
}
