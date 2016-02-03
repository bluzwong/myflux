package com.github.bluzwang.myflux.example.demo;

import com.github.bluzwong.myflux.lib.FluxRequester;


/**
 * Created by bluzwong on 2016/2/3.
 */
public class DemoRequester extends FluxRequester {

    public DemoRequester(String receiverId) {
        super(receiverId);
    }

    public String requestSum(final int a, final int b) {
        return createRequestIO(new RequestAction() {
            @Override
            public void request(final String requestUUID) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createResponse("ccf", requestUUID).setData("sum", a + b).post();
            }
        });
    }

}
