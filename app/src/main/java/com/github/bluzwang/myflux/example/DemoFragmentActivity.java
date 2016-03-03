package com.github.bluzwang.myflux.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.github.bluzwang.myflux_kotlin.R;
import com.github.bluzwong.myflux.lib.FluxCore;
import com.github.bluzwong.myflux.lib.FluxResponse;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;
import flux.Flux;

import java.util.UUID;

public class DemoFragmentActivity extends Activity  {

    DemoFragmentRequester requester;

    TextView tv1,tv2,tv3,tv4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flux);

        // 获取与 this 绑定的请求器
        requester = Flux.getRequester(this, DemoFragmentRequester.class);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);

        // 点击发出请求
        // 该请求将会发出一个type = "2" 的响应
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requester.requestSumIO(1, 2);
            }
        });
    }

    // 接收 type = "1" 的响应时运行
    @ReceiveType(type = {"1"})
    void doCcf(FluxResponse response) {
        int sum = (int) response.getData("sum");
        tv1.setText("type = {\"1\"} => " + sum);
    }

    // 接收 type = "2" 的响应时运行
    @ReceiveType(type = "2")
    void doCcf2(FluxResponse response) {
        int sum = (int) response.getData("sum");
        tv2.setText("type = \"2\" => " + sum);
    }

    // 接收 type = "2" 或者type = "2" 的响应时运行
    @ReceiveType(type = {"1", "2"})
    void dowsd2(FluxResponse response) {
        int sum = (int) response.getData("sum");
        tv3.setText("type = {\"1\", \"2\"} => " + sum);
    }

    // 接收 type = RequestType.REQUEST_1 或者type = RequestType.REQUEST_2 的响应时运行
    @ReceiveType(type = {RequestType.REQUEST_1, RequestType.REQUEST_2})
    void dowsd(FluxResponse response) {
        int sum = (int) response.getData("sum");
        tv4.setText("type = {RequestType.REQUEST_1, RequestType.REQUEST_2} => " + sum);
    }

}
