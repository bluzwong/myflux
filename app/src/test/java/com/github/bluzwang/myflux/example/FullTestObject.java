package com.github.bluzwang.myflux.example;

import com.github.bluzwang.myflux.example.demo.FullReceiverObject;
import com.github.bluzwong.myflux.lib.BuildConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
/**
 * Created by Bruce-Home on 2016/2/3.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class FullTestObject {

    FullReceiverObject receiver;
    @Before
    public void setUp() throws Exception {
        receiver = new FullReceiverObject();
    }

    @Test
    public void test1() throws InterruptedException {

        String requestUuid = receiver.requester.requestSum(1, 2);

        Thread.sleep(300);
        assertEquals(receiver.receives[0], 3);
        assertEquals(receiver.receives[1], 0);
        assertEquals(receiver.receives[2], 3);
        assertEquals(receiver.receives[3], 3);
    }
}