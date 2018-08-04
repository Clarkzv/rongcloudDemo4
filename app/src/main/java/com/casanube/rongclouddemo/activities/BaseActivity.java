package com.casanube.rongclouddemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.casanube.rongclouddemo.ActivityManager;

/**
 * Created by Andy.Mei on 2018/7/31.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
