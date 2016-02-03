package com.github.bluzwong.myflux.lib;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.UUID;

/**
 * Created by bluzwong on 2016/2/3.
 */
public class FluxRequester {

    protected String receiverId;

    public FluxRequester(String receiverId) {
        this.receiverId = receiverId;
    }

    protected FluxResponse createResponse(String type, String requestUUID) {
        return FluxResponse.create(receiverId, type, requestUUID);
    }

    protected String createUUID() {
        return UUID.randomUUID().toString();
    }

    protected interface RequestAction {
        void request(final String requestUUID);
    }

    protected String createRequest(Scheduler scheduler, final RequestAction action) {
        final String uuid = createUUID();
        Observable.just(action)
                .observeOn(scheduler)
                .doOnNext(new Action1<RequestAction>() {
                    @Override
                    public void call(RequestAction requestAction) {
                        action.request(uuid);
                    }
                }).subscribe();
        return uuid;
    }

    protected String createRequest(final RequestAction action) {
        final String uuid = createUUID();
        action.request(uuid);
        return uuid;
    }

    protected String createRequestIO(final RequestAction action) {
        return createRequest(Schedulers.io(), action);
    }

    protected String createRequestComputation(final RequestAction action) {
        return createRequest(Schedulers.computation(), action);
    }

    protected String createRequestNewThread(final RequestAction action) {
        return createRequest(Schedulers.newThread(), action);
    }

    protected String createRequestMainThread(final RequestAction action) {
        return createRequest(AndroidSchedulers.mainThread(), action);
    }

    protected String createRequestCurrent(final RequestAction action) {
        return createRequest(Schedulers.immediate(), action);
    }
}
