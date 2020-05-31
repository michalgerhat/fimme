package com.michalgerhat.fimme;

import android.content.Context;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

// https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/WebSocketEcho.java

public class SocketManager extends WebSocketListener
{
    private Context context;

    SocketManager(Context context)
    {
        this.context = context;
        this.run();
    }

    private void run()
    {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(0,  TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url("wss://gerhat.cz/fimme-server/").build();
        client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();
    }

    @Override public void onOpen(WebSocket ws, Response res)
    {
        System.out.println("connected");
    }

    @Override public void onMessage(WebSocket ws, String msg)
    {
        System.out.println("MESSAGE: " + msg);
    }

    @Override public void onClosing(WebSocket webSocket, int code, String reason)
    {
        webSocket.close(1000, null);
        System.out.println("CLOSE: " + code + " " + reason);
    }

    @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }

}
