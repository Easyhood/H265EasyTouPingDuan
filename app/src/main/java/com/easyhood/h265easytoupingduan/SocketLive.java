package com.easyhood.h265easytoupingduan;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * 功能：WebSocket连接管理
 * 详细描述：
 * 作者：guan_qi
 * 创建日期：2023-03-21
 */
public class SocketLive {
    private static final String TAG = "SocketLive";
    private SocketCallback socketCallback;
    private MyWebSocketClient myWebSocketClient;
    private WebSocket webSocket;
    private int port;

    /**
     * 构造方法
     * @param socketCallback SocketCallback
     * @param port int
     */
    public SocketLive(SocketCallback socketCallback, int port) {
        this.socketCallback = socketCallback;
        this.port = port;
    }

    /**
     * 开始连接
     */
    public void start() {
        try {
            URI uri = new URI("ws://192.168.0.125:9007");
            myWebSocketClient = new MyWebSocketClient(uri);
            myWebSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * WebSocketClient 实体对象
     */
    private class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            Log.i(TAG, "打开 socket  onOpen: ");
        }

        @Override
        public void onMessage(String s) {
        }
        //不断回调他
        @Override
        public void onMessage(ByteBuffer bytes) {
            Log.i(TAG, "消息长度  : "+bytes.remaining());
            byte[] buf = new byte[bytes.remaining()];
            bytes.get(buf);
            socketCallback.callBack(buf);
        }

        @Override
        public void onClose(int i, String s, boolean b) {
            Log.i(TAG, "onClose: ");
        }

        @Override
        public void onError(Exception e) {
            Log.i(TAG, "onError: ");
        }
    }
    public interface SocketCallback{
        void callBack(byte[] data);
    }
}
