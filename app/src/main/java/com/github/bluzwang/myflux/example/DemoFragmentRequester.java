package com.github.bluzwang.myflux.example;

import com.github.bluzwong.myflux.lib.FluxFragmentRequester;
import com.github.bluzwong.myflux.lib.FluxRequester;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by bluzwong on 2016/2/3.
 */

public class DemoFragmentRequester extends FluxFragmentRequester {


    private int slowAdd(int a, int b, int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return a + b;
    }

    private int slowAdd(int a, int b) {
        return slowAdd(a, b, 100);
    }

    private int bigSlowAdd(int a, int b) {
        return slowAdd(a, b, 2000);
    }

    /**
     * io request work on IO thread, not block main thread
     * 将会在io线程运行

     */
    public String requestSumIO(final int a, final int b) {
        return doRequestIO(new RequestAction() {
            @Override
            public void request(final String requestUUID) {
                int sum = bigSlowAdd(a, b);
                // 获取到结果后需要发送结果给 type = "2" 的响应方法
                createResponse("2", requestUUID).setData("sum", sum).post();
            }
        });
    }

    /**
     * heavy request work on Computation thread, not block main thread
     * 将会在计算线程运行
     */
    public String requestSumComputation(final int a, final int b) {
        return doRequestComputation(new RequestAction() {
            @Override
            public void request(final String requestUUID) {
                int sum = slowAdd(a, b);
                // 获取到结果后需要发送结果给 type = "1" 的响应方法
                createResponse("1", requestUUID).setData("sum", sum).post();
            }
        });
    }

}
