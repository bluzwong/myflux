package com.github.bluzwong.myflux.lib;

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

    protected String createRequest(RequestAction action) {
        String uuid = createUUID();
        action.request(uuid);
        return uuid;
    }
}
