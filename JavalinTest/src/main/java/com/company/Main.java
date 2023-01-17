package com.company;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static j2html.TagCreator.*;

public class Main
{
    private static final Map<WsContext, String> userUsernameMap = new ConcurrentHashMap<>();
    private static int nextUserNumber = 1;
    private static ObjectMapper mapper = new ObjectMapper();
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        Javalin app = Javalin.create().start(4000);

        app.get("/", context -> {
            System.out.println("Saul Goodman");
            context.result("Saul Goodman");
        });

        app.get("/list", context -> Controller.getAllVals(context));

        app.get("/list/{special}", context -> Controller.getSpecialVal(context));

        app.ws("/chat", ws -> {
            System.out.println("Websocket Online");
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
