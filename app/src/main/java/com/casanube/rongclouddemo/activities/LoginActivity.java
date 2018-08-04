package com.casanube.rongclouddemo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.casanube.rongclouddemo.MainActivity;
import com.casanube.rongclouddemo.R;

import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private static Map<String, String> USERS = new HashMap<>();

    static {
        USERS.put("cas100000001", "uhdwsXu+1XxmNVwVjJlTZ8geudRMP9sVOqprnJ1UDEZ11T3n/M3z42Oi2amuDuNjesohvDOnIYZ2k88xZOUslLwgnA/dQilc");
        USERS.put("cas100000002", "CmQCPwh0VJAec/G/iaHGr/QW/o4Lv5LeSkaIPoep4I/NXrNbnLb5bCp2RDrAmuJMlvIPyAEWvMg+BLpDCtJel+vxnGdnyIc+");
        USERS.put("cas100000003", "k8TaH/vtkpk0UtwNWCMrb43spkFWfDmCq+irNNSzTzHNim+RHxQjoKJYBC5YNAKhK9U8RapHVSwye5mPBLNCWYgIteY93br9");
    }

    private static String loginAction = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        Button btn1 = (Button) findViewById(R.id.email_sign_in_button);
        Button btn2 = (Button) findViewById(R.id.email_sign_in_button2);
        Button btn3 = (Button) findViewById(R.id.sign_in_button3);
        Button btn4 = (Button) findViewById(R.id.sign_in_button4);
        btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAction = "0";
                attemptLogin("cas100000001");
            }
        });
        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAction = "0";
                attemptLogin("cas100000002");
            }
        });
        btn3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAction = "1";
                attemptLogin("cas100000001");
            }
        });
        btn4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAction = "1";
                attemptLogin("cas100000002");
            }
        });

        findViewById(R.id.sign_in_button5).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction = "0";
                attemptLogin("cas100000003");
            }
        });

    }

    private void attemptLogin(String userId) {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putString("USER_ID", userId).commit();
        String token = USERS.get(userId);
        RongIM.connect(token, callback);

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
            if ("1".equals(loginAction)) {
                Map<String, Boolean> supportedConversation = new HashMap<>();
                supportedConversation.put(Conversation.ConversationType.PRIVATE.getName(), false);
                RongIM.getInstance().startConversationList(getApplicationContext(), supportedConversation);
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

        }

        @Override
        public void onError(final RongIMClient.ErrorCode e) {
            Log.d(TAG, "ConnectCallback connect onError-ErrorCode=" + e);
        }
    };

    private boolean mIsExit;

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}

