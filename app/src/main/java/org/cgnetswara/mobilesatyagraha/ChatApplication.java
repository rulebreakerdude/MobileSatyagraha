package org.cgnetswara.mobilesatyagraha;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class ChatApplication extends Application {

    private static volatile Socket mSocket;

    static Socket getSocket() {
        if(mSocket==null) {
            synchronized (ChatApplication.class) {
                if (mSocket == null) {
                    try {
                        mSocket = IO.socket("http://192.168.1.104:5000");
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return mSocket;
    }
}
