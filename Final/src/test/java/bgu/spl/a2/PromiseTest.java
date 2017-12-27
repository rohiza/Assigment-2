package bgu.spl.a2;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class PromiseTest {

    volatile Boolean flag;

    @Test(timeout=10000)
    public void testMethods() {
        // Initializing
        Promise<String> newProms = new Promise<>();
        AtomicInteger counter = new AtomicInteger(0);

        // Creating callbacks code
        callback callBackCode = ()->{
            counter.incrementAndGet();
        };

        // Subscribing callbacks to promise
        for(int i = 0;i < 20;i++){
            newProms.subscribe(callBackCode);
        }

        // Testing isResolve method
        assertEquals(false,newProms.isResolved());

        // Asserting get method throws IllegalStateException before resolved
        try{
            newProms.get();
            Assert.fail();
        }
        catch(IllegalStateException exp){
        }

        // Testing resolve method
        newProms.resolve("You are resolved");
        try{
            newProms.resolve("You are again resolved");
            Assert.fail();
        }
        catch(IllegalStateException exp){
        }

        // Testing resolve and subscribe methods
        assertEquals(20,counter.get());
        assertEquals("You are resolved",newProms.get());
        newProms.subscribe(callBackCode);
        assertEquals(21,counter.get());
    }

    @Test(timeout=13000)
    public void testThreadSafe() {
        // Initializing
        Promise<String> newProms = new Promise<>();
        Promise<String> newProms2 = new Promise<>();
        AtomicInteger counter = new AtomicInteger(0);
        this.flag = true;

        // Creating callbacks code
        callback callBackCode = ()->{
            counter.incrementAndGet();
        };

        // Creating thread subscribe test code for each thread
        Runnable subsTest = ()->{
            while (this.flag) {
            }
            newProms.subscribe(callBackCode);
        };

        // Creating thread resolve test code for each thread
        Runnable resolveTest = ()->{
            while (this.flag) {
            }
            try {
                newProms.resolve("You are resolved");
            }
            catch(IllegalStateException exp){
                counter.incrementAndGet();
            }
        };

        // Creating thread subscribe test code for each thread
        Runnable subsTest2 = ()->{
            while (this.flag) {
            }
            newProms2.subscribe(callBackCode);
        };

        // Creating thread resolve test code for each thread
        Runnable resolveTest2 = ()-> {
            while (this.flag) {
            }
            newProms2.resolve("You are resolved");
        };

        // Starting threads for subscribe test
        List<Thread> threads = A.initiateThreads(100,subsTest);

        // Executing subscribe method in all threads
        this.flag = false;

        // Asserting all threads finished
        A.assertAllThreadsFinished(threads);

        // Initializing before resolve test
        this.flag = true;

        // Starting threads for resolve test
        threads = A.initiateThreads(100,resolveTest);

        // Executing resolve method in all threads
        this.flag = false;

        // Asserting all threads finished
        A.assertAllThreadsFinished(threads);

        // Initializing before resolve test
        this.flag = true;

        // Starting threads for subscribe test
        List<Thread> threadsSub = A.initiateThreads(100,subsTest2);
        List<Thread> threadsResolve = A.initiateThreads(1,resolveTest2);

        // Executing resolve method in all threads
        this.flag = false;

        // Asserting all threads finished
        A.assertAllThreadsFinished(threadsSub);
        A.assertAllThreadsFinished(threadsResolve);

        // Asserting tests succeeded
        assertEquals(299,counter.get());
    }

}