package com.github.bluzwang.myflux.example;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import com.github.bluzwang.myflux_kotlin.R;

import com.github.bluzwong.myflux.lib.BaseFluxActivity;
import com.github.bluzwong.myflux.lib.FluxDispatcher;
import com.github.bluzwong.myflux.lib.FluxStore;

import java.util.Map;
import java.util.Random;

public class MainActivity extends BaseFluxActivity {

    MainStore store = new MainStore(FluxDispatcher.INSTANCE);
    static MainRequester requester = new MainRequester();

    Button btnAdd, btnRemove;
    ScrollView scrollView;
    TextView textView;
    ProgressDialog dialog;
    /**
     * 当前activity使用的layout,代替setContentView()
     * @return
     */
    @Override
    protected int provideContentId() {
        return R.layout.activity_main;
    }

    /**
     * 当前activity对应的store 一个activity对应一个store实例
     * @return
     */
    @Override
    protected FluxStore provideStore() {
        return store;
    }

    /**
     * 初始化控件或其他组件
     */
    @Override
    protected void init() {
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnRemove = (Button) findViewById(R.id.btn_remove);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        textView = (TextView) findViewById(R.id.text_View);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryRequestAdd();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryRemove();
            }
        });
    }

    /**
     * 当该activity是第一次初始化时调用
     * onNewView() 与 onRestoreView() 只会调用其一
     * 当store 为null时则永远调用 onNewView()
     */
    @Override
    protected void onNewView() {
        tryRequestAdd();
    }

    /**
     * 当该activity从系统恢复时调用,比如屏幕旋转后
     */
    @Override
    protected void onRestoreView() {
        if (store.isRequesting) {
            showLoadingDialog();
        }
    }

    /**
     * 收到请求的反馈时的响应
     * @param type
     * @param dataMap
     */
    @Override
    protected void onResponse(String type, Map<String, Object> dataMap) {
        switch (type) {
            case RequestType.REQUEST_ADD:
                store.isRequesting = false;
                dismissLoadingDialog();

            case RequestType.RESTORE_UI:
                textView.setText(store.text);
                break;
            default:break;
        }
    }

    private void tryRequestAdd() {
        if (!store.isRequesting) {
            store.isRequesting = true;
            showLoadingDialog();
            requester.requestAdd(hashCode(), "random data : " + new Random().nextInt(100));
        }
    }

    private void tryRemove() {
        if (store.isRequesting) {
            return;
        }

        int size = store.datas.size();
        if (size > 0) {
            store.datas.remove(size - 1);
            store.createString();
            textView.setText(store.text);
        }
    }

    private void showLoadingDialog() {
        if (dialog == null) {
            dialog = ProgressDialog.show(this, "Loading", "loading...", true, false);
        } else if (!dialog.isShowing()){
            dialog.show();
        }
    }

    private void dismissLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
