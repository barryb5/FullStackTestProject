package com.company;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static j2html.TagCreator.*;

public class Main
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Controller controller = new Controller();

        Javalin app = Javalin.create().start(4000);

        app.get("/", context -> {
            System.out.println("Saul Goodman");
            context.result("Saul Goodman");
        });


        app.ws("/chat/{chatName}", ws -> controller.chatHandler(ws));

        app.ws("/getChats/{chatID}", ws -> controller.getChats(ws));

        DatabaseManager manager = new DatabaseManager();
        manager.resetDatabase();
        manager.addChat("test1");
        manager.addMessage("test1", "asdf", "text message");

    }
}
