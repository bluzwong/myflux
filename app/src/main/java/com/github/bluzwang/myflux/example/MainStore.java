package com.github.bluzwang.myflux.example;


import com.github.bluzwong.myflux.lib.FluxDispatcher;
import com.github.bluzwong.myflux.lib.FluxStore;
import com.github.bluzwong.myflux.lib.*;
import com.github.bluzwong.myflux.lib.SavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangzhijie@wind-mobi.com on 2015/9/25.
 * store内持有activity的数据,不能在此进行耗时的操作,也不直接操纵ui,通过发送消息(responsePostToUi)来告知ui更新界面
 * 数据可以是activity直接读写,或者是接收到的requester发送的结果
 */
public class MainStore extends FluxStore {

    // 这些字段都是activity中的数据,由activity对应的store来持有,并且维护状态
    // 带有@Maintain的字段将会在屏幕旋转后自动恢复
    @Maintain
    List<String> datas = new ArrayList<>();

    @Maintain
    String text;

    @Maintain
    boolean isRequesting = false;

    // 事件调度器
    FluxDispatcher dispatcher;

    public MainStore(FluxDispatcher dispatcher) {
        super(dispatcher);
        this.dispatcher = dispatcher;
    }

    // requester完成后通过时间调度器发送的消息会到此接受
    // 这里只进行简单的数据处理 保存,然后发消息给ui(也就是activity)更新界面
    // 一个耗时请求变成数据的流向 UI => Requester => Store => UI
    // 一个不耗时的请求的流向 UI <=> Store
    @Override
    protected void onRequestDone(String type, Map<String, Object> dataMap) {
        // 这里的type就是requester通过 dispatcher.dispatchRequestToStoreWithType() 发送的type
        switch (type) {
            case RequestType.REQUEST_ADD:
                String result = (String) dataMap.get(RequestType.KEY_DATA);
                datas.add(result);
                createString();

                responsePostToUi(type);
                break;
            default:break;
        }
    }

    // 屏幕旋转后数据恢复完成时调用,可以发送消息给ui恢复界面
    @Override
    protected void onDataRestored(SavedData savedData) {
        responsePostToUi(RequestType.RESTORE_UI);
    }

    public String createString() {
        StringBuilder buffer = new StringBuilder();
        for (String data : datas) {
            buffer.append(data).append("\n");
        }
        text = buffer.toString();
        return text;
    }
}
