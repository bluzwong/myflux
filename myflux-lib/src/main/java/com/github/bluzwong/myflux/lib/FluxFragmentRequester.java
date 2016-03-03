package com.github.bluzwong.myflux.lib;

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
 * should not override the default constructor
 */
public class FluxFragmentRequester extends Fragment {

    public static final String FLUX_RECEIVER_KEY = "FLUX_RECEIVER_KEY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        getReceiverId();
    }

    private void init(FragmentManager fragmentManager, String tag) {
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        if (fragmentByTag != null && fragmentByTag instanceof FluxFragmentRequester && fragmentByTag == this) {
            getReceiverId();
            return;
        }
        fragmentManager.beginTransaction().add(this, tag).commit();
    }


    private static FluxFragmentRequester getRequesterIfExists(FragmentManager fragmentManager, String tag) {
        Fragment fragmentByTag = fragmentManager.findFragmentByTag(tag);
        if (fragmentByTag instanceof FluxFragmentRequester) {
            return (FluxFragmentRequester) fragmentByTag;
        }
        return null;
    }

    /**
     * if fragment manager contains multi flux requesters, use tag to mark
     * @param fragmentManager
     * @param clz
     * @param tag
     * @param <T>
     * @return
     */
    public static <T extends FluxFragmentRequester> T getRequesterOrCreate(FragmentManager fragmentManager, Class<T> clz, String tag) {
        FluxFragmentRequester requester = getRequesterIfExists(fragmentManager, tag);
        if (requester == null || !clz.isInstance(requester)) {
            try {
                requester = clz.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(FLUX_RECEIVER_KEY, createUUID());
                requester.setArguments(bundle);
                requester.init(fragmentManager, tag);
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
        if (receiverId == null || receiverId.equals("")) {
            receiverId = getArguments().getString(FLUX_RECEIVER_KEY);
        }
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

    protected FluxResponse newFluxResponse(String type, String requestUUID) {
        return FluxResponse.create(receiverId, type, requestUUID);
    }

    protected FluxResponse buildFluxResponse(String type, String requestUUID) {
        return FluxResponse.create(receiverId, type, requestUUID);
    }

    protected FluxResponse makeFluxResponse(String type, String requestUUID) {
        return FluxResponse.create(receiverId, type, requestUUID);
    }

    protected static String createUUID() {
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
