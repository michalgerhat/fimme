package com.michalgerhat.fimme;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthPreferences
{
    private static final String PREFS_NAME = "fimme_auth";
    private static final String USERNAME = "fimme_username";
    private static final String TOKEN = "fimme_token";

    private SharedPreferences preferences;

    public AuthPreferences(Context context)
    {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getUsername()
    {
        return preferences.getString(USERNAME, null);
    }

    public String getToken()
    {
        return preferences.getString(TOKEN, null);
    }

    public void setUsername(String username)
    {
        preferences.edit().putString(USERNAME, username).apply();
    }

    public void setToken(String token)
    {
        preferences.edit().putString(TOKEN, token).apply();
    }

    public boolean isLoginStored()
    {
        return (this.getUsername() != null && this.getToken() != null);
    }

    public void clearUserData()
    {
        preferences.edit().clear().apply();
    }
}