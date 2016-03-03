package com.github.bluzwong.myflux.lib;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.UUID;

/**
 * Created by bluzwong on 2016/2/3.
 */
public abstract class FluxRequester extends Fragment {

    public static final String FLUX_REQUESTER_TAG = "FLUX_REQUESTER_TAG";
    public static final String FLUX_RECEIVER_KEY = "FLUX_RECEIVER_KEY";

    public FluxRequester() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        receiverId = createUUID();
        Bundle bundle = new Bundle();
        bundle.putString(FLUX_RECEIVER_KEY, receiverId);
        setArguments(bundle);
    }

    private void init(FragmentManager fragmentManager) {
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(FLUX_REQUESTER_TAG);
        if (fragmentByTag != null && fragmentByTag instanceof FluxRequester && fragmentByTag == this) {
            if (receiverId == null || receiverId.equals("")) {
                receiverId = getArguments().getString(FLUX_RECEIVER_KEY);
            }
            return;
        }
        fragmentManager.beginTransaction().add(this, FLUX_REQUESTER_TAG).commit();
    }


    private static FluxRequester _getRequester(FragmentManager fragmentManager) {
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(FLUX_REQUESTER_TAG);
        if (fragmentByTag instanceof FluxRequester) {
            return (FluxRequester) fragmentByTag;
        }
        return null;
    }

    public static <T extends FluxRequester> T getRequester(FragmentManager fragmentManager, Class<T> clz) {
        FluxRequester requester = _getRequester(fragmentManager);
        if (requester == null || !clz.isInstance(requester)) {
            try {
                requester = clz.newInstance();
                requester.init(fragmentManager);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (T) requester;

    }

    private String receiverId;

    public String getReceiverId() {
        return receiverId;
    }

    /**
     * after request done, create the response to receiver
     *
     * @param type        the request type
     *                    example:
     *                    createResponse("Custom Request Type", requestUUID).setData("key", "value").post();
     *                    <p/>
     *                    will post to the class registed to FluxCore and have this method:
     *                    <p/>
     *                    at ReceiveType(type = {"Custom Request Type"})
     *                    public void receive(FluxResponse response) {
     *                    String valueByKey = response.getData("key"); // valueByKey : "value"
     *                    String type = response.getType();            // type : "Custom Request Type"
     *                    String UUID = response.getRequestUUID();     // UUID : requestUUID
     *                    }
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
     *
     * @param scheduler work on which thread
     * @param action    the real request
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
