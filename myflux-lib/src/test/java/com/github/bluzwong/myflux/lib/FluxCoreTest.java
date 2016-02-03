package com.github.bluzwong.myflux.lib;

import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;
import com.hwangjr.rxbus.RxBus;
import org.junit.After;
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
public class FluxCoreTest {

    @Test
    public void testInitAndDestroy() throws Exception {
        RxBus.get().unregister(FluxCore.INSTANCE);
        RxBus.get().register(FluxCore.INSTANCE);
        try {
            RxBus.get().register(FluxCore.INSTANCE);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }


    @Test
    public void testRegister() throws Exception {
        Map<String, FluxReceiver> maps = FluxCore.INSTANCE.receiverMaps;
        maps.clear();

        FluxReceiver receiver = new FluxReceiver() {
            @Override
            public void onReceive(Map<String, Object> dataMap, String type, String requestUUID) {

            }
        };
        FluxCore.INSTANCE.register("ccf", receiver);
        assertTrue(maps.containsKey("ccf"));
        assertEquals(maps.get("ccf"), receiver);
    }

    @Test
    public void testUnregister() throws Exception {
        Map<String, FluxReceiver> maps = FluxCore.INSTANCE.receiverMaps;
        maps.clear();

        FluxReceiver receiver = new FluxReceiver() {
            @Override
            public void onReceive(Map<String, Object> dataMap, String type, String requestUUID) {

            }
        };
        FluxCore.INSTANCE.register("ccf", receiver);
        assertTrue(maps.containsKey("ccf"));
        assertEquals(maps.get("ccf"), receiver);

        FluxCore.INSTANCE.unregister("wsd", receiver);
        assertEquals(maps.get("ccf"), receiver);

        FluxCore.INSTANCE.unregister("ccf", new FluxReceiver() {
            @Override
            public void onReceive(Map<String, Object> dataMap, String type, String requestUUID) {

            }
        });
        assertEquals(maps.get("ccf"), receiver);
        FluxCore.INSTANCE.unregister("ccf", receiver);
        assertEquals(maps.get("ccf"), null);
    }

    @Test
    public void testOnReceiveResponse() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        FluxCore.INSTANCE.register("ccf", new FluxReceiver() {
            @Override
            public void onReceive(Map<String, Object> dataMap, String type, String requestUUID) {
                assertEquals(type, "ccf-type");
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

    CountDownLatch latch;
    @Test
    public void testSwitchReceiveType() throws Exception {
        latch = new CountDownLatch(2);
        FluxCore.INSTANCE.register("ccf-id", new FluxReceiver() {
            @Override
            public void onReceive(Map<String, Object> dataMap, String type, String requestUUID) {
                FluxCore.switchReceiveTypeReflect(FluxCoreTest.this, dataMap, type);
            }
        });

        FluxResponse.create("ccf-id", "wsd", "uuid").setData("key", "value").post();
        long startTime = System.currentTimeMillis();

        latch.await(200, TimeUnit.MILLISECONDS);
        long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 200);
    }

    @ReceiveType(type = "wsd")
    public void receiveTest(Map<String, Object> maps, String type) {
        assertEquals(maps.get("key"), "value");
        latch.countDown();
    }

    @ReceiveType(type = "wsd")
    public void receiveTest(Map<String, Object> maps) {
        assertEquals(maps.get("key"), "value");
        latch.countDown();
    }

}