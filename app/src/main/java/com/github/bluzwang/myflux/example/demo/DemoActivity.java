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
        FluxCore.INSTANCE.register(this, receiverUUID);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requester.requestSum(1, 2);
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
        FluxCore.switchReceiveType(this, dataMap, type);
    }

    @ReceiveType(type = {"ccf"})
    void doCcf(Map<String, Object> dataMap) {
        int sum = (int) dataMap.get("sum");
        Toast.makeText(this, "a + b => " + sum, Toast.LENGTH_SHORT).show();
    }

    @ReceiveType(type = "ccf")
    void doCcf(Map<String, Object> dataMap, String type) {
        int sum = (int) dataMap.get("sum");
        Toast.makeText(this, "a + b 2=> " + sum, Toast.LENGTH_SHORT).show();
    }

    @ReceiveType(type = {RequestType.REQUEST_ADD, RequestType.RESTORE_UI})
    void dowsd(Map<String, Object> dataMap, String type) {
        int sum = (int) dataMap.get("sum");
        Toast.makeText(this, "a + b 2=> " + sum, Toast.LENGTH_SHORT).show();
    }


}
