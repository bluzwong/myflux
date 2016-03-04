package com.github.bluzwang.myflux.example;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.bluzwang.myflux_kotlin.R;
import com.github.bluzwong.myflux.lib.FluxResponse;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;
import flux.Flux;

/**
 * Created by Bruce-Home on 2016/3/4.
 */
public class DemoFragment extends Fragment {

    private DemoFragmentRequester requester;
    TextView tv1,tv2,tv3,tv4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requester = Flux.getRequester(this, DemoFragmentRequester.class, savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_flux, container, false);
        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv2 = (TextView) view.findViewById(R.id.tv2);
        tv3 = (TextView) view.findViewById(R.id.tv3);
        tv4 = (TextView) view.findViewById(R.id.tv4);

        TextView tv = (TextView) view.findViewById(R.id.tv);
        tv.setText(new StringBuilder().append("this fragment => ").append(Integer.toHexString(this.hashCode())).append("\nrequester => ").append(Integer.toHexString(requester.hashCode())));


        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requester.requestSumIO(1, 2);
            }
        });

        view.findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requester.cancelAll();
            }
        });


        if (savedInstanceState != null) {
            String tv2Text = savedInstanceState.getString("tv2", "");
            tv2.setText(tv2Text);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Flux.fluxOnSaveInstanceState(requester, outState);
        super.onSaveInstanceState(outState);
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
