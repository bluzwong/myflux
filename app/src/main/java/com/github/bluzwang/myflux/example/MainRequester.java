package com.github.bluzwang.myflux.example;

import com.github.bluzwong.myflux.lib.FluxDispatcher;

/**
 * Created by Bruce-Home on 2015/10/28.
 */
public class MainRequester {
    FluxDispatcher dispatcher = FluxDispatcher.INSTANCE;
    public void requestAdd(int hashCode, String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    String result = data + " -> after request";

                    dispatcher.dispatchRequestToStoreWithType(RequestType.REQUEST_ADD, hashCode)
                            .key(RequestType.KEY_DATA).toValue(result)
                            .post();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
