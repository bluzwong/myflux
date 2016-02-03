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
        return createRequest(new FluxRequester.RequestAction() {
            @Override
            public void request(final String requestUUID) {
                Observable.just(null)
                        .map(new Func1<Object, Integer>() {
                            @Override
                            public Integer call(Object nil) {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                return a + b;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                createResponse("ccf", requestUUID).setData("sum", integer).post();
                            }
                        });
            }
        });
    }

}
