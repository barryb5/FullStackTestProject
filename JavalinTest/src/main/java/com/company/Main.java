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
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Javalin app = Javalin.create().start(4000);

        app.get("/", context -> {
            System.out.println("Saul Goodman");
            context.result("Saul Goodman");
        });


        app.ws("/chat", ws -> Controller.chatHandler(ws));

        app.ws("/getChats", ws -> Controller.getChats(ws));
    }


}
