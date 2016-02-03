package com.github.bluzwang.myflux.example.demo;

import com.github.bluzwang.myflux.example.RequestType;
import com.github.bluzwong.myflux.lib.FluxCore;
import com.github.bluzwong.myflux.lib.FluxReceiver;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;

import java.util.Map;
import java.util.UUID;

public class DemoReceiver2 implements FluxReceiver {

    String receiverUUID = UUID.randomUUID().toString();
    DemoRequester requester;

    public DemoReceiver2() {
        requester = new DemoRequester(receiverUUID);
        FluxCore.INSTANCE.register(receiverUUID, this);
    }


    @Override
    public void onReceive(Map<String, Object> dataMap, String type, String requestUUID) {
        FluxCore.switchReceiveTypeReflect(this, dataMap, type);
    }

    int[] receives = {0, 0, 0, 0};

    public void clearReceives() {
        for (int i = 0; i < receives.length; i++) {
            receives[i] = 0;
        }
    }

    @ReceiveType(type = {"1"})
    void doCcf(Map<String, Object> dataMap) {
        int sum = (int) dataMap.get("sum");
        receives[0] = sum;

    }

    @ReceiveType(type = "2")
    void doCcf(Map<String, Object> dataMap, String type) {
        int sum = (int) dataMap.get("sum");
        receives[1] = sum;
    }

    @ReceiveType(type = {"1", "2"})
    void dowsd(Map<String, Object> dataMap, String type) {
        int sum = (int) dataMap.get("sum");
        receives[2] = sum;
    }

    @ReceiveType(type = {RequestType.REQUEST_ADD, RequestType.RESTORE_UI})
    void dowsd(Map<String, Object> dataMap) {
        int sum = (int) dataMap.get("sum");
        receives[3] = sum;
    }

}
