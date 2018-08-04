package com.casanube.rongclouddemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.casanube.rongclouddemo.R;

/**
 * Created by Andy.Mei on 2018/7/30.
 */

public class ConversationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        String title = getIntent().getData().getQueryParameter("title");
        TextView textView = (TextView) this.findViewById(R.id.chart_title);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        textView.setText(title);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}