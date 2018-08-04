package com.casanube.rongclouddemo;

import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.casanube.rongclouddemo.activities.BaseActivity;
import com.casanube.rongclouddemo.adapter.SessionListAdapter;
import com.casanube.rongclouddemo.interf.MessageObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imkit.utils.StringUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private List<Map> sessionList = new ArrayList<>();
    private Map<String, Map> sessionIdx = new HashMap<>();
    private Map<String, Integer> positionIdx = new HashMap<>();

    private Handler handler;

    private String[] getSessionList() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String userId = sp.getString("USER_ID", "");
        if("cas100000001".equals(userId)) return new String[]{"cas100000002", "cas100000003"};
        if("cas100000002".equals(userId)) return new String[]{"cas100000001", "cas100000003"};
//        return new String[]{"32298", "42035"};
        return new String[0];
    }

    ;

    ListView listView;
    SessionListAdapter adapter;

    RongIMClient.ResultCallback<List<Message>> resultCallback = new RongIMClient.ResultCallback<List<Message>>() {
        @Override
        public void onSuccess(List<Message> messages) {
            Log.d(TAG, "message arrived, count:" + messages.size());
            Integer pos = null;
            for (Message msg : messages) {
                String sender = msg.getSenderUserId();
                MessageContent contentMsg = msg.getContent();
                updateOneSession(sender, contentMsg);

            }
        }

        @Override
        public void onError(RongIMClient.ErrorCode errorCode) {
            Log.e(TAG, "errorCode:" + errorCode);

        }
    };

    private void updateOneSession(String userId, MessageContent contentMsg) {
        String content = getMsgDisplay(contentMsg);

        Integer pos = positionIdx.get(userId);
        Map map = sessionIdx.get(userId);
        if (map == null) return;
        map.put("lastMsg", content);
        Log.d(TAG, "msg- from:" + userId + ",content:" + content);
        if (pos != null) updateSingle(pos, content);
    }

    private String getMsgDisplay(MessageContent messageContent) {
        String content;
        if (messageContent instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
            content = textMessage.getContent();
        } else if (messageContent instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
            content = "图片消息";
        } else if (messageContent instanceof VoiceMessage) {//语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
            content = "语音消息";
        } else if (messageContent instanceof RichContentMessage) {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
            content = "图文消息";
        } else {
            Log.d(TAG, "onSent-其他消息，自己来判断处理");
            content = "其他消息";
        }
        if (content != null && content.length() > 20)
            content = content.substring(0, 20) + "...";
        return content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int idx = 0;
        for (String user : getSessionList()) {
            Map map = new HashMap();
            map.put("userId", user);
            sessionList.add(map);
            sessionIdx.put(user, map);
            positionIdx.put(user, idx++);
        }
        adapter = new SessionListAdapter(MainActivity.this, R.layout.session_list, sessionList);
        this.listView = (ListView) findViewById(R.id.list_view);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map item = sessionList.get(position);
                String userId = (String) item.get("userId");
                RongIM.getInstance().startPrivateChat(getApplicationContext(), userId, "与" + userId + "对话");
            }
        });
        App app = (App) getApplication();
        app.resgister(new MessageObserver() {
            @Override
            public void update(Message message) {
                handler.obtainMessage(1, message).sendToTarget();
            }
        });
        for (Map.Entry<String, Integer> entry : positionIdx.entrySet()) {
            getHistMesssage(entry.getKey(), resultCallback);
        }
        this.handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 1) {
                    Message message = (Message) msg.obj;
                    if (message != null) {
                        String sender = message.getSenderUserId();
                        MessageContent contentMsg = message.getContent();
                        updateOneSession(sender, contentMsg);
                    }
                }
            }
        };
        //获取本地存储会话列表()
        getConversactionList();
    }


    private void updateSingle(int position, String msg) {
        Log.d(TAG, "position:" + position);
        View view = listView.getChildAt(position);
        if (view == null) return;
        TextView textView = (TextView) view.findViewById(R.id.last_msg);
        textView.setText(msg);
    }

    private void getHistMesssage(String userId, RongIMClient.ResultCallback<List<Message>> resultCallback) {
        Log.d(TAG, "getHistMesssage, userId:" + userId);
        RongIMClient.getInstance().getHistoryMessages(Conversation.ConversationType.PRIVATE, userId, -1, 1, resultCallback);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RongIM.getInstance().disconnect();
    }

    private void getConversactionList() {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations == null) return;
                for (Conversation con : conversations) {
                    String info = String.format("sendUser:%s, lastMsg:%s", con.getSenderUserId(), con.getLatestMessage());
                    Log.d(TAG, "conversations: " + info);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        }, Conversation.ConversationType.PRIVATE);
    }
}
