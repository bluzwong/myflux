package com.github.bluzwang.myflux.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import com.github.bluzwang.myflux_kotlin.R;


import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity {


    Button btnAdd, btnRemove;
    ScrollView scrollView;
    TextView textView;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        init();
    }

    /**
     * 初始化控件或其他组件
     */
    protected void init() {
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnRemove = (Button) findViewById(R.id.btn_remove);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        textView = (TextView) findViewById(R.id.text_View);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击添加按钮尝试添加
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击移除按钮 尝试移除最后一条
            }
        });
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
