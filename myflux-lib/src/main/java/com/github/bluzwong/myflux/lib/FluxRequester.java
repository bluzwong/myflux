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

    /**
     * after request done, create the response to receiver
     * @param type the request type
     *   example:
     *             createResponse("Custom Request Type", requestUUID).setData("key", "value").post();
     *
     *     will post to the class registed to FluxCore and have this method:
     *
     *             at ReceiveType(type = {"Custom Request Type"})
     *             public void receive(FluxResponse response) {
     *                 String valueByKey = response.getData("key"); // valueByKey : "value"
     *                 String type = response.getType();            // type : "Custom Request Type"
     *                 String UUID = response.getRequestUUID();     // UUID : requestUUID
     *             }
     *
     * @param requestUUID the unique request id
     * @return response instance, need invoke post() to post it
     */
    protected FluxResponse createResponse(String type, String requestUUID) {
        return FluxResponse.create(receiverId, type, requestUUID);
    }

    protected String createUUID() {
        return UUID.randomUUID().toString();
    }

    protected interface RequestAction {
        void request(final String requestUUID);
    }

    /**
     * do request at scheduler
     * @param scheduler work on which thread
     * @param action the real request
     * @return the unique id of each request
     */
    protected String doRequest(Scheduler scheduler, final RequestAction action) {
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

    protected String doRequest(final RequestAction action) {
        final String uuid = createUUID();
        action.request(uuid);
        return uuid;
    }

    protected String doRequestIO(final RequestAction action) {
        return doRequest(Schedulers.io(), action);
    }

    protected String doRequestComputation(final RequestAction action) {
        return doRequest(Schedulers.computation(), action);
    }

    protected String doRequestNewThread(final RequestAction action) {
        return doRequest(Schedulers.newThread(), action);
    }

    protected String doRequestMainThread(final RequestAction action) {
        return doRequest(AndroidSchedulers.mainThread(), action);
    }

    protected String doRequestCurrent(final RequestAction action) {
        return doRequest(Schedulers.immediate(), action);
    }
}
