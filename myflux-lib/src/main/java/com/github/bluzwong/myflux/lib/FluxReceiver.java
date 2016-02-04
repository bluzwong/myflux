package com.github.bluzwong.myflux.lib;

/**
 * Created by bluzwong on 2016/2/3.
 */
public interface FluxReceiver {
    void onReceive(FluxResponse response);
}
