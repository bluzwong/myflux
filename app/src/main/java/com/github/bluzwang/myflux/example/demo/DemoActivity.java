package com.github.bluzwang.myflux.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.github.bluzwang.myflux.example.RequestType;
import com.github.bluzwang.myflux_kotlin.R;
import com.github.bluzwong.myflux.lib.FluxCore;
import com.github.bluzwong.myflux.lib.FluxReceiver;
import com.github.bluzwong.myflux.lib.FluxResponse;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;

import java.util.Map;
import java.util.UUID;

public class DemoActivity extends Activity  {

    String receiverUUID = UUID.randomUUID().toString();
    DemoRequester requester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String uuid = savedInstanceState.getString("uuid", "");
            if (uuid != null && !uuid.equals("")) {
                receiverUUID = uuid;
            }
        }
        setContentView(R.layout.activity_flux);
        requester = new DemoRequester(receiverUUID);
        FluxCore.INSTANCE.register(receiverUUID, this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requester.requestSumDelay(1, 2);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("uuid", receiverUUID);
        super.onSaveInstanceState(outState);
    }

    //@Override
    public void onReceive(FluxResponse response, Map<String, Object> dataMap) {
        // 使用apt解析注解
//        FluxCore.switchReceiveTypeApt(this, dataMap, type);
        // 使用反射解析注解 效率没有apt高
//        FluxCore.switchReceiveTypeReflect(this, dataMap, type);

        // 优先使用apt apt不可用时 使用反射
        FluxCore.switchReceiveType(this, response);
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

    @ReceiveType(type = {RequestType.REQUEST_ADD, RequestType.RESTORE_UI})
    void dowsd(FluxResponse response) {
        int sum = (int) response.getData("sum");
        receives[3] = sum;
    }

}
