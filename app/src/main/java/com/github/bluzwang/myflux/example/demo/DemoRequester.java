package com.github.bluzwang.myflux.example.demo;

import com.github.bluzwong.myflux.lib.FluxRequester;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by bluzwong on 2016/2/3.
 */

public class DemoRequester extends FluxRequester {

    public DemoRequester(String receiverId) {
        super(receiverId);
    }

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
     * request work on the current thread, may block main thread if the current is;
     */
    public String requestSum(final int a, final int b) {
        return doRequest(new RequestAction() {
            @Override
            public void request(final String requestUUID) {
                /// will block main thread
                int sum = slowAdd(a, b);
                createResponse("1", requestUUID).setData("sum", sum).post();
            }
        });
    }


    /**
     * request work on the current thread, use rxjava to do request
     */
    public String requestSum2(final int a, final int b) {
        return doRequest(new RequestAction() {
            @Override
            public void request(final String requestUUID) {
                Observable.just(requestUUID)
                        .observeOn(Schedulers.io())
                        .map(new Func1<String, Integer>() {
                            @Override
                            public Integer call(String s) {
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                return a+b;
                            }
                        })
                        .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        createResponse("1", requestUUID).setData("sum", integer).post();
                    }
                });
            }
        });
    }

    /**
     * io request work on IO thread, not block main thread
     */
    public String requestSumIO(final int a, final int b) {
        return doRequestIO(new RequestAction() {
            @Override
            public void request(final String requestUUID) {
                int sum = bigSlowAdd(a, b);
                createResponse("2", requestUUID).setData("sum", sum).post();
            }
        });
    }

    /**
     * heavy request work on Computation thread, not block main thread
     */
    public String requestSumComputation(final int a, final int b) {
        return doRequestComputation(new RequestAction() {
            @Override
            public void request(final String requestUUID) {
                int sum = slowAdd(a, b);
                createResponse("1", requestUUID).setData("sum", sum).post();
            }
        });
    }

}
