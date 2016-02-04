package com.github.bluzwong.myflux.lib;

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
public class FluxResponseTest {


    @Test
    public void testCreate() throws Exception {
        FluxResponse response = FluxResponse.create("ccf", "type-ccf", "ccf-uuid");
        assertEquals(response.getReceiverId(), "ccf");
        assertEquals(response.getType(), "type-ccf");
        assertEquals(response.getRequestUUID(), "ccf-uuid");
        Map<String, Object> map = response.getDataMap();
        assertNotNull(map);
        assertEquals(map.size(), 0);
    }

    @Test
    public void testSetData() throws Exception {
        FluxResponse response = FluxResponse.create("ccf", "ccf-type", "ccf-uuid").setData("ccf-key", "ccf-data");
        Map<String, Object> map = response.getDataMap();
        assertNotNull(map);
        assertEquals(map.size(), 1);
        assertTrue(map.containsKey("ccf-key"));
        assertEquals(map.get("ccf-key"), "ccf-data");
        response.setData("wsd-key", "wsd-data");
        assertEquals(map.size(), 2);
        response.setData("wsd-key", "wsd-data");
        assertEquals(map.size(), 2);
        assertEquals(map.get("wsd-key"), "wsd-data");
        assertEquals(map.get("ccf-key"), "ccf-data");
    }

    @Test
    public void testPost() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        FluxCore.INSTANCE.register("ccf", new FluxReceiver() {
            @Override
            public void onReceive(FluxResponse response, Map<String, Object> dataMap) {
                assertEquals(response.getType(), "ccf-type");
                assertEquals(dataMap.get("ccf-key"), "ccf-data");
                assertEquals("main", Thread.currentThread().getName());
                latch.countDown();
            }
        });
        FluxResponse.create("ccf", "ccf-type", "ccf-uuid").setData("ccf-key", "ccf-data").post();
        long startTime = System.currentTimeMillis();
        latch.await(200, TimeUnit.MILLISECONDS);
        long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 200);
    }

    @Test
    public void testPost2() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        FluxCore.INSTANCE.register("ccf", new FluxReceiver() {
            @Override
            public void onReceive(FluxResponse response, Map<String, Object> dataMap) {
                latch.countDown();
            }
        });
        FluxResponse.create("wsd", "ccf-type", "ccf-uuid").setData("ccf-key", "ccf-data").post();
        long startTime = System.currentTimeMillis();
        latch.await(200, TimeUnit.MILLISECONDS);
        long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime >= 200);
    }

    @Test
    public void testPost3() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        FluxReceiver receiver = new FluxReceiver() {
            @Override
            public void onReceive(FluxResponse response, Map<String, Object> dataMap) {
                latch.countDown();
            }
        };
        FluxCore.INSTANCE.register("ccf", receiver);

        FluxCore.INSTANCE.unregister("ccf", receiver);
        FluxResponse.create("ccf", "ccf-type", "ccf-uuid").setData("ccf-key", "ccf-data").post();
        long startTime = System.currentTimeMillis();
        latch.await(200, TimeUnit.MILLISECONDS);
        long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime >= 200);
    }


    @Test
    public void testPostMulti() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        FluxReceiver receiver = new FluxReceiver() {
            @Override
            public void onReceive(FluxResponse response, Map<String, Object> dataMap) {
                assertTrue(false);
            }
        };
        FluxCore.INSTANCE.register("ccf", receiver);
        FluxCore.INSTANCE.register("ccf", new FluxReceiver() {
            @Override
            public void onReceive(FluxResponse response, Map<String, Object> dataMap) {
                latch.countDown();
            }
        });
        FluxResponse.create("ccf", "ccf-type", "ccf-uuid").setData("ccf-key", "ccf-data").post();
        long startTime = System.currentTimeMillis();
        latch.await(200, TimeUnit.MILLISECONDS);
        long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 200);
    }
}