package com.github.bluzwong.myflux.lib.switchtype;

import com.github.bluzwong.myflux.lib.FluxResponse;


/**
 * Created by bluzwong on 2016/2/3.
 */
public interface ReceiveTypeDispatcher {
    void dispatchType(Object target, FluxResponse fluxResponse);
}
