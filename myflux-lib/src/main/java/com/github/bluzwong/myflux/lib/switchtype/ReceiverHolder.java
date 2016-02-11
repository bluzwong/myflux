package com.github.bluzwong.myflux.lib.switchtype;

import com.github.bluzwong.myflux.lib.FluxReceiver;

import java.lang.ref.WeakReference;

/**
 * Created by Bruce-Home on 2016/2/5.
 */
public class ReceiverHolder {
    private WeakReference<FluxReceiver> interfaceReceiver;


    private FluxReceiver proxyReceiver;

    private WeakReference<Object> objectReceiver;

    private ReceiverHolder() { }

    public static ReceiverHolder createNormal(FluxReceiver realReceiver) {
        ReceiverHolder holder = new ReceiverHolder();
        holder.interfaceReceiver = new WeakReference<FluxReceiver>(realReceiver);
        return holder;
    }

    public static ReceiverHolder createProxy(FluxReceiver proxyReceiver, Object realReceiver) {
        ReceiverHolder holder = new ReceiverHolder();
        holder.proxyReceiver = proxyReceiver;
        holder.objectReceiver = new WeakReference<Object>(realReceiver);
        return holder;
    }

    public FluxReceiver getReceiver() {
        if (interfaceReceiver != null ) {
            return interfaceReceiver.get();
        }

        if (objectReceiver == null || objectReceiver.get() == null) {
            return null;
        }

        if (proxyReceiver != null) {
            return proxyReceiver;
        }

        return null;
    }

    public Object getRegisteredReceiver() {
        if (interfaceReceiver != null) {
            return interfaceReceiver.get();
        }
        if (objectReceiver == null) {
            return null;
        }

        return objectReceiver.get();
    }
}
