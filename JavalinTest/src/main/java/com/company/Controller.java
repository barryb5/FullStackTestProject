package com.company;

import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;


import java.lang.constant.Constable;
import java.util.Arrays;

public class Controller {

    private Controller(){}

    static String[] returnVals = {"asdf", "ksljdfoie", "slkdfj"};

     public static void getAllVals(Context context) {
        context.json(returnVals);
    }

    public static void getSpecialVal(Context context) {
        if (context.pathParam("special") != null) {
            context.result(context.pathParam("special"));
        } else {
            context.result("No special entered");
        }
    }

    public static void webSocketHandler(WsConfig ws, Context context) {

    }

}
