package com.casanube.rongclouddemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.casanube.rongclouddemo.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andy.Mei on 2018/7/30.
 */


public class SessionListAdapter extends ArrayAdapter {

    private final int resourceId;
    private List<Map> data;
    private Map<String, Integer> idx = new HashMap<>();


    public SessionListAdapter(Context context, int textViewResourceId, List<Map> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("SessionListAdapter", this.data.toString());
        Map map = (Map) getItem(position);
        Log.d("SessionListAdapter", map.toString());
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView title = (TextView) view.findViewById(R.id.session_title);//获取该布局内的图片视图
        TextView lastMsg = (TextView) view.findViewById(R.id.last_msg);//获取该布局内的文本视图
        String userId = (String)map.get("userId");
        String msg = (String)map.get("lastMsg");
        if(userId != null){
            title.setText(userId);
            idx.put(userId, position);
        }
        if(msg != null) lastMsg.setText(msg);
        return view;
    }
}

