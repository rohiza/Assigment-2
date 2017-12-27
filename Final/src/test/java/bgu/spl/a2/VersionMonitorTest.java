package bgu.spl.a2;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class VersionMonitorTest {
    volatile Boolean flag;

    @Test(timeout=10000)
    public void testGetVersionAndInc() {
        // Initializing
        VersionMonitor versM = new VersionMonitor();

        // Asserting version number is initialized and incremented correctly
        for (int i = 0; i < 40; i++){
            assertEquals(java.util.Optional.ofNullable(i), versM.getVersion());
            versM.inc();
        }
    }

    @Test(timeout=10000)
    public void testIncAndWait() {
        // Initializing
        VersionMonitor versM = new VersionMonitor();
        AtomicInteger counter = new AtomicInteger(0);
        int currVer = versM.getVersion();

        // Creating thread test code for each thread
        Runnable awaitTest = ()->{
            versM.await(currVer);
            counter.incrementAndGet();
        };

        // Starting threads for test
        List<Thread> threads = A.initiateThreads(100,awaitTest);

        // Trying to interrupt threads in await in order to check only exit by inc method
        for(int i = 0;i < threads.size();i++){
            synchronized (versM) {
                versM.notify();
            }
        }

        // Asserting all threads were not awaken by the interrupts
        assertEquals(0, counter.get());

        // Changing version
        versM.inc();

        // Asserting all threads finished
        A.assertAllThreadsFinished(threads);

        // Asserting all threads were awaken by inc method
        assertEquals(100, counter.get());
    }

    @Test(timeout=10000)
    public void testThreadSafe() {
        // Initializing
        VersionMonitor versM = new VersionMonitor();
        this.flag = true;

        // Creating thread test code for each thread
        Runnable incTest = ()->{
            while (this.flag) {
            }
            versM.inc();
        };

        // Starting threads for test
        List<Thread> threads = A.initiateThreads(100,incTest);

        // Executing inc method in all threads
        this.flag = false;

        // Asserting all threads finished
        A.assertAllThreadsFinished(threads);

        // Asserting all threads were awaken by inc method
        assertEquals(java.util.Optional.of(100), versM.getVersion());
    }

}