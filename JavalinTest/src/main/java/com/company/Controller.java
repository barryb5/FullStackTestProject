package com.company;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;


import java.lang.constant.Constable;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Controller {
    private static int nextUserNumber = 1;
    private static final Map<WsContext, String> userUsernameMap = new ConcurrentHashMap<>();
    private static ObjectMapper mapper = new ObjectMapper();

    private Controller() {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }
    public static void chatHandler(WsConfig ws) {
        System.out.println("Chat Websocket Online");
        ws.onConnect(ctx -> {
            String username = "User" + nextUserNumber++;
            System.out.println(username + " Joined");
            userUsernameMap.put(ctx, username);
            broadcastMessage("Server", (username + " joined the chat"), MessageType.SERVER_UPDATE);
        });
        ws.onMessage(ctx -> {
            String message = ctx.message();
            System.out.println(message);
            System.out.println(ctx.toString());
            broadcastMessage(userUsernameMap.get(ctx), message, MessageType.CLIENT_MESSAGE);
        });
    }

    public static void getChats(WsConfig ws) {
        System.out.println("Stream Websocket Online");
        ws.onConnect(ctx -> {
            System.out.println("New Listener Joined");
            broadcastMessage("Server", ("Another user is listening"), MessageType.SERVER_UPDATE);
        });
    }

    private static void broadcastMessage(String sender, String message, MessageType type) {
        userUsernameMap.keySet().stream().filter(ctx -> ctx.session.isOpen()).forEach(session -> {
            try {
                if (type == MessageType.SERVER_UPDATE) {
                    session.send(createJsonMessageFromServer("Server", message));
                } else if (type == MessageType.CLIENT_MESSAGE) {
                    session.send(createJsonMessageFromSender(sender, message));
                } else {

                }
            } catch (JsonProcessingException e) {
                System.out.println("Error");
                throw new RuntimeException(e);
            }
        });
    }

    private static String createJsonMessageFromServer(String sender, String message) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new Message(sender, message));
    }
    private static String createJsonMessageFromSender(String sender, String message) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new Message(sender, message));
    }

}
