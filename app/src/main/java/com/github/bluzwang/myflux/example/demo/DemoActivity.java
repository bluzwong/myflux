package com.github.bluzwang.myflux.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.github.bluzwang.myflux.example.RequestType;
import com.github.bluzwang.myflux_kotlin.R;
import com.github.bluzwong.myflux.lib.FluxCore;
import com.github.bluzwong.myflux.lib.FluxReceiver;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;

import java.util.Map;
import java.util.UUID;

public class DemoActivity extends Activity implements FluxReceiver {

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

    @Override
    public void onReceive(Map<String, Object> dataMap, String type, String requestUUID) {
        // 使用apt解析注解
//        FluxCore.switchReceiveTypeApt(this, dataMap, type);
        // 使用反射解析注解 效率没有apt高
//        FluxCore.switchReceiveTypeReflect(this, dataMap, type);

        // 优先使用apt apt不可用时 使用反射
        FluxCore.switchReceiveType(this, dataMap, type);
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
        Toast.makeText(this, "1111 a + b => " + sum, Toast.LENGTH_SHORT).show();
    }

    @ReceiveType(type = "2")
    void doCcf(Map<String, Object> dataMap, String type) {
        int sum = (int) dataMap.get("sum");
        receives[1] = sum;
        Toast.makeText(this, "2222 a + b 2=> " + sum, Toast.LENGTH_SHORT).show();
    }

    @ReceiveType(type = {"1", "2"})
    void dowsd(Map<String, Object> dataMap, String type) {
        int sum = (int) dataMap.get("sum");
        receives[2] = sum;
        Toast.makeText(this, "3333 a + b 2=> " + sum, Toast.LENGTH_SHORT).show();
    }

    @ReceiveType(type = {RequestType.REQUEST_ADD, RequestType.RESTORE_UI})
    void dowsd(Map<String, Object> dataMap) {
        int sum = (int) dataMap.get("sum");
        receives[3] = sum;
        Toast.makeText(this, "4444 a + b 2=> " + sum, Toast.LENGTH_SHORT).show();
    }

}
