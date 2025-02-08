package com.example.sunnycontroller;

public interface WebSocketEventListener {
    void onOpen();
    void onTextReceived(String message);
    void onBinaryReceived(byte[] data);
    void onPingReceived(byte[] data);
    void onPongReceived(byte[] data);
    void onException(Exception e);
    void onCloseReceived();
}
