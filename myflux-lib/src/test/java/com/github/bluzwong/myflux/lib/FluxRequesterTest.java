package com.github.bluzwong.myflux.lib;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by Bruce-Home on 2016/2/3.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class FluxRequesterTest {
        FluxRequester requester;

    @Before
    public void setUp() throws Exception {
        requester = new FluxRequester("id");
    }

    @Test
    public void testCreateResponse() throws Exception {
        FluxResponse response = requester.createResponse("type", "uuid");
        assertEquals(response.getType(), "type");
        assertEquals(response.getRequestUUID(), "uuid");
        assertEquals(response.getReceiverId(), "id");
    }

    @Test
    public void testCreateRequest() throws Exception {
        long startTime = System.currentTimeMillis();
        final String[] requestUUID1 = new String[2];
        final CountDownLatch latch = new CountDownLatch(1);
        FluxCore.INSTANCE.register("id", new FluxReceiver() {
            @Override
            public void onReceive(Map<String, Object> dataMap, String type, String requestUUID) {
                assertNotNull(requestUUID);
                assertNotEquals(requestUUID, "");
                assertEquals(type, "typeccf");
                assertEquals(dataMap.get("key"), "ccf");
                requestUUID1[1] = requestUUID;
                latch.countDown();
            }
        });

        String uuid = requester.createRequest(new FluxRequester.RequestAction() {
            @Override
            public void request(String requestUUID) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                requestUUID1[0] = requestUUID;
                requester.createResponse("typeccf", requestUUID).setData("key", "ccf").post();
            }
        });

        long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 200);


        startTime = System.currentTimeMillis();
        latch.await(500, TimeUnit.MILLISECONDS);
        endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 500);
        assertEquals(uuid, requestUUID1[0]);
        assertEquals(uuid, requestUUID1[1]);
    }

}