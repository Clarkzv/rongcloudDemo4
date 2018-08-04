package com.casanube.rongclouddemo;

import android.app.Application;
import android.util.Log;

import com.casanube.rongclouddemo.interf.MessageObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by Andy.Mei on 2018/7/30.
 */

public class App extends Application {

    private static String TAG = "APP";

    List<MessageObserver> listener = new ArrayList<>();

    public void resgister(MessageObserver observer){
        this.listener.add(observer);
    }

    private void fireChange(Message message){
        for(MessageObserver observer: listener){
            observer.update(message);
        }
    }

    private RongIMClient.ConnectCallback callback = new RongIMClient.ConnectCallback() {
        @Override
        public void onTokenIncorrect() {
            /**
             * token过期 刷新token
             *
             */
            Log.e(TAG, "onTokenIncorrect");
        }

        @Override
        public void onSuccess(String s) {

            Log.d(TAG, "onSuccess:" + s);
        }

        @Override
        public void onError(final RongIMClient.ErrorCode e) {
            Log.d(TAG, "ConnectCallback connect onError-ErrorCode=" + e);
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "init app");
        super.onCreate();
        RongIM.init(this);
        RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener());
    }

    private class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {

        @Override
        public boolean onReceived(Message message, int left) {
            Log.d(TAG, "onreceive msg:" + message.getSenderUserId());
            fireChange(message);
            return false;
        }
    }
}
