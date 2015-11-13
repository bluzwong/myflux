package com.github.bluzwang.myflux.example;

import com.github.bluzwong.myflux.lib.FluxDispatcher;

/**
 * Created by Bruce-Home on 2015/10/28.
 */

/**
 * 在这里做耗时的数据请求(网络或者数据库等)
 *  一个耗时请求数据的流向 UI => Requester => Store => UI
 *  一个不耗时的请求的流向 UI <=> Store
 */
public class MainRequester {
    // 事件调度器
    FluxDispatcher dispatcher = FluxDispatcher.INSTANCE;

    // 方法内使用异步的方式请求数据,凡是耗时的任务全要在此进行
    public String requestAdd(final int hashCode, final String data) {
        final String uuid = FluxDispatcher.createUUID();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 模拟请求,处理数据的耗时
                    Thread.sleep(5000);
                    String result = data + " -> after request";

                    // 处理完成后 通过调度器发送消息给store,处理完的数据通过 key value来传递, 这里的type就是store中接收的type.
                    // 这里的hashcode是发送者的hashcode,也就是activity传入的hashCode().内部使用hashcode来判断每个请求的拥有者.
                    dispatcher.dispatchRequestToStoreWithType(RequestType.REQUEST_ADD, hashCode)
                            .withData(RequestType.KEY_DATA, result)
                            .post(uuid);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return uuid;
    }
}
