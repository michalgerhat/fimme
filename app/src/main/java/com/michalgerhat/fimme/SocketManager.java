package com.michalgerhat.fimme;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

// https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/WebSocketEcho.java

public class SocketManager extends WebSocketListener
{
    public interface SocketListener
    {
        void onConnected();
        void onLoginAccepted(JSONObject data);
        void onLoginDenied();
    }

    private SocketListener listener;

    void setListener(SocketListener listener) { this.listener = listener; }

    private Context context;
    private AuthPreferences authPreferences;
    private WebSocket ws;
    private String accessToken;

    SocketManager(Context context)
    {
        this.listener = new SocketListener()
        {
            @Override
            public void onConnected() {}
            @Override
            public void onLoginAccepted(JSONObject data) {}
            @Override
            public void onLoginDenied() {}
        };

        this.context = context;
        this.authPreferences = new AuthPreferences(this.context);
        this.accessToken = null;
        this.run();
    }

    public void setAccessToken(String token)
    {
        accessToken = token;
    }

    private void run()
    {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(0,  TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url("wss://gerhat.cz/fimme-server/").build();
        client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();
    }

    public void sendMessage(String channel, JSONObject data)
    {
        try
        {
            JSONObject msg = new JSONObject();
            msg.put("token", accessToken);
            msg.put("channel", channel);
            msg.put("data", data);
            System.out.println("SENDING: " + channel + " " + msg.toString());
            ws.send(msg.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(String channel, String data)
    {
        try
        {
            JSONObject msg = new JSONObject();
            msg.put("token", accessToken);
            msg.put("channel", channel);
            msg.put("data", data);
            System.out.println("SENDING: " + channel + " " + msg.toString());
            ws.send(msg.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override public void onOpen(WebSocket ws, Response res)
    {
        this.ws = ws;
        System.out.println("connected");
        listener.onConnected();
    }

    @Override public void onMessage(WebSocket ws, String message)
    {
        System.out.println("MESSAGE: " + message);
        try
        {
            JSONObject msg = new JSONObject(message);
            String channel = msg.getString("channel");

            switch (channel)
            {
                case "login-accepted":
                    listener.onLoginAccepted(msg.getJSONObject("data"));
                    break;

                case "login-denied":
                    listener.onLoginDenied();
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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
