package com.example.sunnycontroller;

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

import dev.gustavoavila.websocketclient.WebSocketClient;


public class WebSocketSingleton {
    private static WebSocketSingleton instance;
    private WebSocketClient webSocketClient;
    private WebSocketEventListener eventListener;
    private boolean isConnected = false;

    private WebSocketSingleton() {
    }

    public static WebSocketSingleton getInstance() {
        if (instance == null) {
            synchronized (WebSocketSingleton.class) {
                if (instance == null) {
                    instance = new WebSocketSingleton();
                }
            }
        }
        return instance;
    }

    public void setEventListener(WebSocketEventListener listener) {
        this.eventListener = listener;
    }

    public void sendMessage(String message) {
        if (webSocketClient != null && isConnected == true) {
            webSocketClient.send(message);
        }
    }

    public void closeConnection () {
        webSocketClient.close(1000,1000, "keyfi");
        isConnected = false;
    }
    public boolean connectToWebSocket(String serverUrl) {
        try {
            webSocketClient = new WebSocketClient(new URI(serverUrl)) {
                @Override
                public void onOpen() {
                    Log.d("WebSocket", "Bağlantı başlatıldı.");
                    if (eventListener != null) {
                        eventListener.onOpen();
                        isConnected = true;
                    }
                }

                @Override
                public void onTextReceived(String message) {
                    if (eventListener != null) {
                        eventListener.onTextReceived(message);
                    }
                }

                @Override
                public void onBinaryReceived(byte[] data) {
                    if (eventListener != null) {
                        eventListener.onBinaryReceived(data);
                    }
                }

                @Override
                public void onPingReceived(byte[] data) {
                    if (eventListener != null) {
                        eventListener.onPingReceived(data);
                    }
                }

                @Override
                public void onPongReceived(byte[] data) {
                    if (eventListener != null) {
                        eventListener.onPongReceived(data);
                    }
                }

                @Override
                public void onException(Exception e) {
                    if (eventListener != null) {
                        eventListener.onException(e);
                    }
                }

                @Override
                public void onCloseReceived(int reason, String description) {
                    if (eventListener != null) {
                        eventListener.onCloseReceived();
                        isConnected = false;
                    }
                }
            };

            webSocketClient.setConnectTimeout(5000);
            webSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return isConnected;
    }
}