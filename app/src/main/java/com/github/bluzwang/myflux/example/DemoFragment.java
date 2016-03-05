package com.github.bluzwang.myflux.example;

import android.app.Activity;
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
    String msg1,msg2,msg3,msg4;

    public DemoFragment() {
        setArguments(new Bundle());
    }

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

        Bundle arguments = getArguments();
        if (savedInstanceState != null) {
           loadStates(savedInstanceState);
        } else if (arguments != null) {
            loadStates(arguments);
        }
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();

    }


    void loadStates(Bundle bundle) {
        msg1 = bundle.getString("msg1", "not saved");
        msg2 = bundle.getString("msg2", "not saved");
        msg3 = bundle.getString("msg3", "not saved");
        msg4 = bundle.getString("msg4", "not saved");
        tv1.setText(msg1);
        tv2.setText(msg2);
        tv3.setText(msg3);
        tv4.setText(msg4);
    }
    void saveStates() {
        saveStates(getArguments());
    }


    void saveStates(Bundle bundle) {
        bundle.putString("msg1",msg1);
        bundle.putString("msg2",msg2);
        bundle.putString("msg3",msg3);
        bundle.putString("msg4",msg4);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Flux.fluxOnSaveInstanceState(requester, outState);
        saveStates(outState);
    }

    // 接收 type = "1" 的响应时运行
    @ReceiveType(type = {"1"})
    void doCcf(FluxResponse response) {
        int sum = (int) response.getData("sum");
        msg1 = "type = {\"1\"} => " + sum;
        saveStates();
        tv1.setText(msg1);
    }

    // 接收 type = "2" 的响应时运行
    @ReceiveType(type = "2")
    void doCcf2(FluxResponse response) {
        int sum = (int) response.getData("sum");
        msg2 = "type = \"2\" => " + sum;
        saveStates();
        tv2.setText(msg2);
    }

    // 接收 type = "2" 或者type = "2" 的响应时运行
    @ReceiveType(type = {"1", "2"})
    void dowsd2(FluxResponse response) {
        int sum = (int) response.getData("sum");
        msg3 = "type = {\"1\", \"2\"} => " + sum;
        saveStates();
        tv3.setText(msg3);
    }

    // 接收 type = RequestType.REQUEST_1 或者type = RequestType.REQUEST_2 的响应时运行
    @ReceiveType(type = {RequestType.REQUEST_1, RequestType.REQUEST_2})
    void dowsd(FluxResponse response) {
        int sum = (int) response.getData("sum");
        msg4 = "type = {RequestType.REQUEST_1, RequestType.REQUEST_2} => " + sum;
        saveStates();
        tv4.setText(msg4);
    }
}
