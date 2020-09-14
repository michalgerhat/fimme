package com.michalgerhat.fimme;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONObject;

public class FimmeService extends Service
{
    public class LocalBinder extends Binder
    {
        FimmeService getService()
        {
            return FimmeService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }

    private SocketManager socket;
    private AuthPreferences authPreferences;

    public boolean loggedIn = false;

    public FimmeService()
    {
        this.socket = new SocketManager(this);
        this.authPreferences = new AuthPreferences(this);

        socket.setListener(new SocketManager.SocketListener()
        {
            @Override
            public void onConnected()
            {
                toast("Connected!");
                if (authPreferences.isLoginStored())
                    socket.sendMessage("authenticate", authPreferences.getToken());
                else
                {

                }
            }
            @Override
            public void onLoginAccepted(JSONObject data)
            {
                loggedIn = true;
                toast("Login successful!");
            }
            @Override
            public void onLoginDenied()
            {
                loggedIn = false;
                toast("Wrong credentials!");
            }
        });
    }

    public void requestLogin(JSONObject credentials)
    {
        socket.sendMessage("request-login", credentials);
    }

    public void toast(final String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
