package bgu.spl.a2.sim;

import bgu.spl.a2.A;
import bgu.spl.a2.Promise;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class SuspendingMutexTest {
    @Test(timeout=10000)
    public void testThreadSafeAndOneAtA_Time(){
        Computer comp = new Computer("A");
        comp.successSig = 1;
        comp.failSig = 0;

        Integer numThreads = 100;

        SuspendingMutex sus = new SuspendingMutex(comp);
        CountDownLatch latchForMutex = new CountDownLatch(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        Runnable computerUseWithMutex = () -> {
            tryToAcquire(sus);
            latchForMutex.countDown();
        };

        Runnable computerUse = () -> {
            comp.checkAndSign(new Vector<>(),new HashMap<>());
            latch.countDown();
        };

        A.initiateThreads(numThreads ,computerUseWithMutex);

        A.initiateThreads(numThreads + 900 ,computerUse);

        try {
            latchForMutex.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(0,latch.getCount());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void tryToAcquire(SuspendingMutex sus){
        Promise promise = sus.down();

        if(promise.isResolved() && promise.get() != null){
            ((Computer)promise.get()).checkAndSign(new Vector<>(),new HashMap<>());
            sus.up();
        }
        else{
            CountDownLatch latch = new CountDownLatch(1);

            promise.subscribe(() -> latch.countDown());

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tryToAcquire(sus);
        }

    }
}