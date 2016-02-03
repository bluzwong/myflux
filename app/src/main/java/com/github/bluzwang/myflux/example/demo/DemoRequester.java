package com.github.bluzwang.myflux.example.demo;

import com.github.bluzwong.myflux.lib.FluxRequester;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
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

    public String requestSum(final int a, final int b) {
        return createRequest(new RequestAction() {
            @Override
            public void request(final String requestUUID) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createResponse("1", requestUUID).setData("sum", a + b).post();
            }
        });
    }
    public String requestSum2(final int a, final int b) {
        return createRequest(new RequestAction() {
            @Override
            public void request(final String requestUUID) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createResponse("2", requestUUID).setData("sum", a + b).post();
            }
        });
    }

    public String requestSum3(final int a, final int b) {
        return createRequest(new RequestAction() {
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
                        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        createResponse("1", requestUUID).setData("sum", integer).post();
                    }
                });
            }
        });
    }

    public String requestSumDelay(final int a, final int b) {
        return createRequestIO(new RequestAction() {
            @Override
            public void request(final String requestUUID) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createResponse("2", requestUUID).setData("sum", a + b).post();
            }
        });
    }
}
