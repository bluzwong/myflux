package com.github.bluzwang.myflux.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.github.bluzwang.myflux_kotlin.R;
import com.github.bluzwong.myflux.lib.FluxCore;
import com.github.bluzwong.myflux.lib.FluxResponse;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;

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

        // 请求的实际运行对象，需要绑定接受响应对象的id
        requester = new DemoRequester(receiverUUID);
        // 注册接受响应的id，指定接受到响应的类
        FluxCore.INSTANCE.register(receiverUUID, this);

        // 点击发出请求
        // 该请求将会发出一个type = "2" 的响应
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requester.requestSumIO(1, 2);
            }
        });
    }

    // 注册后 将会在接收到 type = "1" 的响应时运行在主线程
    @ReceiveType(type = {"1"})
    void doCcf(FluxResponse response) {
        int sum = (int) response.getData("sum");
        Toast.makeText(this, "type = {\"1\"} sum => " + sum, Toast.LENGTH_SHORT).show();
    }

    // 注册后 将会在接收到 type = "2" 的响应时运行在主线程
    @ReceiveType(type = "2")
    void doCcf2(FluxResponse response) {
        int sum = (int) response.getData("sum");
        Toast.makeText(this, "type = \"2\" sum => " + sum, Toast.LENGTH_SHORT).show();
    }

    // 注册后 将会在接收到 type = "2" 或者type = "2" 的响应时运行在主线程
    @ReceiveType(type = {"1", "2"})
    void dowsd2(FluxResponse response) {
        int sum = (int) response.getData("sum");
        Toast.makeText(this, "type = {\"1\", \"2\"} sum => " + sum, Toast.LENGTH_SHORT).show();
    }

    // 注册后 将会在接收到 type = RequestType.REQUEST_1 或者type = RequestType.REQUEST_2 的响应时运行在主线程
    @ReceiveType(type = {RequestType.REQUEST_1, RequestType.REQUEST_2})
    void dowsd(FluxResponse response) {
        int sum = (int) response.getData("sum");
        Toast.makeText(this, "type = {RequestType.REQUEST_1, RequestType.REQUEST_2} sum => " + sum, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("uuid", receiverUUID);
        super.onSaveInstanceState(outState);
    }

}
