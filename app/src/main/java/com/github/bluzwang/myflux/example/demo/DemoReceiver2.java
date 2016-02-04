package com.github.bluzwang.myflux.example.demo;

import com.github.bluzwang.myflux.example.RequestType;
import com.github.bluzwong.myflux.lib.FluxCore;
import com.github.bluzwong.myflux.lib.FluxReceiver;
import com.github.bluzwong.myflux.lib.FluxResponse;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;

import java.util.UUID;

public class DemoReceiver2 implements FluxReceiver {

    String receiverUUID = UUID.randomUUID().toString();
    DemoRequester requester;

    public DemoReceiver2() {
        requester = new DemoRequester(receiverUUID);
        FluxCore.INSTANCE.register(receiverUUID, this);
    }


    @Override
    public void onReceive(FluxResponse response) {
        FluxCore.switchReceiveTypeReflect(this, response);
    }

    int[] receives = {0, 0, 0, 0};

    public void clearReceives() {
        for (int i = 0; i < receives.length; i++) {
            receives[i] = 0;
        }
    }

    @ReceiveType(type = {"1"})
    void doCcf(FluxResponse response) {
        int sum = (int) response.getData("sum");
        receives[0] = sum;

    }

    @ReceiveType(type = "2")
    void doCcf2(FluxResponse response) {
        int sum = (int) response.getData("sum");
        receives[1] = sum;
    }

    @ReceiveType(type = {"1", "2"})
    void dowsd2(FluxResponse response) {
        int sum = (int) response.getData("sum");
        receives[2] = sum;
    }

    @ReceiveType(type = {RequestType.REQUEST_1, RequestType.RESTORE_2})
    void dowsd(FluxResponse response) {
        int sum = (int) response.getData("sum");
        receives[3] = sum;
    }

}
