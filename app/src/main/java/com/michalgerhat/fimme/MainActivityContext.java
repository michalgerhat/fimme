package com.michalgerhat.fimme;

import java.io.Serializable;

public interface MainActivityContext extends Serializable
{
    SocketManager getSocketManager();
}
