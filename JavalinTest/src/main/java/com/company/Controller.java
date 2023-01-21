package com.company;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.constant.Constable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Controller {
    private static int nextUserNumber = 1;
    private static final Map<WsContext, String> userUsernameMap = new ConcurrentHashMap<>();
    private static ObjectMapper mapper = new ObjectMapper();
    private DatabaseManager manager;

    Controller() {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        manager = new DatabaseManager();
    }
    public void chatHandler(WsConfig ws) {
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

    public void getChats(WsConfig ws) {
        System.out.println("GetChats Websocket Online");
        ws.onConnect(ctx -> {
            System.out.println("New getChats Joined");
//            broadcastMessage("Server", ("Another user is listening"), MessageType.SERVER_UPDATE);
        });
        ws.onMessage((ctx) -> {
            if (ctx.message().equalsIgnoreCase("getChatList")) {
                ctx.send(createJsonResultSetFromServer(manager.getChats()));
            } else {
                ctx.send(createJsonResultSetFromServer(manager.getMessages(ctx.message())));
            }
        });
    }

    public void getMessages() {

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
    private static JSONArray createJsonResultSetFromServer(ResultSet results) throws JsonProcessingException, SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = results.getMetaData();
        while(results.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i=1; i<=numColumns; i++) {
                String column_name = rsmd.getColumnName(i);
                obj.put(column_name, results.getObject(column_name));
            }
            json.put(obj);
        }
        System.out.println(json);
        return json;
    }
    private static String createJsonMessageFromSender(String sender, String message) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new Message(sender, message));
    }

}
