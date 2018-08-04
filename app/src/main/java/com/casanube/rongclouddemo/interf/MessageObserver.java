package com.casanube.rongclouddemo.interf;

import io.rong.imlib.model.Message;

/**
 * Created by Andy.Mei on 2018/7/31.
 */

public interface MessageObserver {

    public void update(Message message);
}
