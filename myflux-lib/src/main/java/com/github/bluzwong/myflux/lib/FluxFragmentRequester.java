package com.github.bluzwong.myflux.lib;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by bluzwong on 2016/2/3.
 * should not override the default constructor
 */
public class FluxFragmentRequester extends Fragment {

    public static final String FLUX_RECEIVER_KEY = "FLUX_RECEIVER_KEY";

    private final Map<String, Subscription> requestingMap = new WeakHashMap<String, Subscription>();
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
                //bundle.putString();
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
        return FluxResponse.create(receiverId, type, requestUUID)
                .cancel(!requestingMap.containsKey(requestUUID));
    }

    protected FluxResponse newFluxResponse(String type, String requestUUID) {
        return FluxResponse.create(receiverId, type, requestUUID)
                .cancel(!requestingMap.containsKey(requestUUID));
    }

    protected FluxResponse buildFluxResponse(String type, String requestUUID) {
        return FluxResponse.create(receiverId, type, requestUUID)
                .cancel(!requestingMap.containsKey(requestUUID));
    }

    protected FluxResponse makeFluxResponse(String type, String requestUUID) {
        return FluxResponse.create(receiverId, type, requestUUID)
                .cancel(!requestingMap.containsKey(requestUUID));
    }

    protected static String createUUID() {
        return UUID.randomUUID().toString();
    }

    public void cancel(String uuid) {
        if (!requestingMap.containsKey(uuid)) {
            // uuid not matched
            return;
        }

        Subscription subscription = requestingMap.get(uuid);
        if (subscription == null) {
            // request is null
            return;
        }
        if (!subscription.isUnsubscribed()) {
            // can unsubscribe
            subscription.unsubscribe();
            //return;
        }
        if (requestingMap.containsKey(uuid)) {
            requestingMap.remove(uuid);
        }
    }

    public void cancelAll() {
        for (Map.Entry<String, Subscription> kv : requestingMap.entrySet()) {
            if (kv.getValue().isUnsubscribed()) {
                continue;
            }
            kv.getValue().unsubscribe();
        }
        requestingMap.clear();
    }

    protected interface RequestAction {
        void request(final String requestUUID);
    }

    /**
     * do request at scheduler
     * sync can not cancel
     * @param scheduler work on which thread
     * @param action    the real request
     * @return the unique id of each request
     */
    private String doRequest(Scheduler scheduler, final RequestAction action) {
        final String uuid = createUUID();
        Subscription subscription = Observable.just(action)
                .observeOn(scheduler)
                .map(new Func1<RequestAction, Object>() {
                    @Override
                    public Object call(RequestAction requestAction) {
                        action.request(uuid);
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        requestingMap.put(uuid, subscription);
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

}
