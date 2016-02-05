package com.github.bluzwong.myflux.lib;

import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;
import com.hwangjr.rxbus.RxBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.lang.ref.WeakReference;
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
        Map<String, WeakReference<FluxReceiver>> maps = FluxCore.INSTANCE.receiverMaps;
        maps.clear();

        FluxReceiver receiver = new FluxReceiver() {
            @Override
            public void onReceive(FluxResponse response) {

            }
        };
        FluxCore.INSTANCE.register("ccf", receiver);
        assertTrue(maps.containsKey("ccf"));
        assertEquals(maps.get("ccf").get(), receiver);
    }

    @Test
    public void testUnregister() throws Exception {
        Map<String, WeakReference<FluxReceiver>> maps = FluxCore.INSTANCE.receiverMaps;
        maps.clear();

        FluxReceiver receiver = new FluxReceiver() {
            @Override
            public void onReceive(FluxResponse response) {

            }
        };
        FluxCore.INSTANCE.register("ccf", receiver);
        assertTrue(maps.containsKey("ccf"));
        assertEquals(maps.get("ccf").get(), receiver);

        FluxCore.INSTANCE.unregister("wsd", receiver);
        assertEquals(maps.get("ccf").get(), receiver);

        FluxCore.INSTANCE.unregister("ccf", new FluxReceiver() {
            @Override
            public void onReceive(FluxResponse response) {

            }
        });
        assertEquals(maps.get("ccf").get(), receiver);
        FluxCore.INSTANCE.unregister("ccf", receiver);
        assertEquals(maps.get("ccf"), null);
    }

     class TestObject {
        @ReceiveType(type = "testDynamic")
        public void receiveWsd(FluxResponse response) {
            assertEquals(response.getReceiverId(), "aaa");
            assertEquals(response.getType(), "testDynamic");
            assertEquals(response.getRequestUUID(), "ccc");
            latch.countDown();
        }
    }

    @Test
    public void testRegister2() throws Exception {
        Map<String, WeakReference<FluxReceiver>> maps = FluxCore.INSTANCE.receiverMaps;
        maps.clear();
        TestObject object = new TestObject();
        FluxCore.INSTANCE.register("ccf", object);
        FluxReceiver receiverProxy = maps.get("ccf").get();
        assertNotNull(receiverProxy);
        assertNotEquals(object, receiverProxy);
        latch = new CountDownLatch(1);
        receiverProxy.onReceive(FluxResponse.create("aaa", "testDynamic", "ccc"));
        assertTrue(latch.await(200, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testUnregister2() throws Exception {
        Map<String, WeakReference<FluxReceiver>> maps = FluxCore.INSTANCE.receiverMaps;
        maps.clear();
        TestObject object = new TestObject();
        FluxCore.INSTANCE.register("ccf", object);
        FluxReceiver receiverProxy = maps.get("ccf").get();
        assertNotNull(receiverProxy);
        assertNotEquals(object, receiverProxy);

        FluxCore.INSTANCE.unregister("ccf", object);
        assertEquals(maps.get("ccf"), null);
    }

    @Test
    public void testOnReceiveResponse() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        FluxCore.INSTANCE.register("ccf", new FluxReceiver() {
            @Override
            public void onReceive(FluxResponse response) {
                assertEquals(response.getType(), "ccf-type");
                assertEquals(response.getData("ccf-key"), "ccf-data");
                assertEquals("main", Thread.currentThread().getName());
                latch.countDown();
            }
        });
        FluxResponse.create("ccf", "ccf-type", "ccf-uuid").setData("ccf-key", "ccf-data").post();
        assertTrue(latch.await(200, TimeUnit.MILLISECONDS));
    }

    CountDownLatch latch;
    @Test
    public void testSwitchReceiveType() throws Exception {
        latch = new CountDownLatch(2);
        FluxCore.INSTANCE.register("ccf-id", new FluxReceiver() {
            @Override
            public void onReceive(FluxResponse response) {
                FluxCore.switchReceiveTypeReflect(FluxCoreTest.this, response);
            }
        });

        FluxResponse.create("ccf-id", "wsd", "uuid").setData("key", "value").post();
        long startTime = System.currentTimeMillis();

        latch.await(200, TimeUnit.MILLISECONDS);
        long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < 200);
    }

    @ReceiveType(type = "wsd")
    public void receiveTest(FluxResponse response) {
        assertEquals(response.getData("key"), "value");
        latch.countDown();
    }

    @ReceiveType(type = "wsd")
    public void receiveTest2(FluxResponse response) {
        assertEquals(response.getData("key"), "value");
        latch.countDown();
    }

}