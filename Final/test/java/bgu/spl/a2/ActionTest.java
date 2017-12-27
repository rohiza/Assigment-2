package bgu.spl.a2;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class ActionTest {
    boolean actionStartExecutedOne = false;
    boolean actionContinuationExecutedOne = false;
    volatile boolean actionStartExecutedTwo = false;
    volatile boolean actionContinuationExecutedTwo = false;
    Vector<Action<Boolean>> actionsToWaitFor = new Vector<>();

    @Test(timeout=10000)
    public void testImportantOperation() throws NoSuchFieldException, IllegalAccessException {
        // Creating test data
        Action<Boolean> actionToWaitFor1 = new Action<Boolean>() {
            @Override
            protected void start() {
                complete(true);
            }
        };

        // Creating test data
        Action<Boolean> actionToWaitFor2 = new Action<Boolean>() {
            @Override
            protected void start() {
                complete(true);
            }
        };

        actionsToWaitFor.add(actionToWaitFor1);
        actionsToWaitFor.add(actionToWaitFor2);

        Action<Boolean> actionOne = new Action<Boolean>() {
            @Override
            protected void start() {
                actionStartExecutedOne = true;

                sendMessage(actionToWaitFor1, "An Actor 2", new PrivateState() {});

                then(actionsToWaitFor,() -> {
                    actionContinuationExecutedOne = true;
                });
            }
        };

        Action<Boolean> actionTwo = new Action<Boolean>() {
            @Override
            protected void start() {
                actionStartExecutedTwo = true;

                then(new Vector<Action<Boolean>>(),() -> {
                    actionContinuationExecutedTwo = true;
                    complete(true);
                });
            }
        };


        ActorThreadPool pool = new ActorThreadPool(4);

        // Initiating test

        assertEquals(false,actionStartExecutedOne);
        assertEquals(false,actionContinuationExecutedOne);

        actionOne.handle(pool, "An Actor 1", new PrivateState() {});

        assertEquals(true,actionStartExecutedOne);
        assertEquals(false,actionContinuationExecutedOne);

        actionOne.handle(pool, "An Actor 1", new PrivateState() {});

        assertEquals(true,actionStartExecutedOne);
        assertEquals(true,actionContinuationExecutedOne);

        Field field = ActorThreadPool.class.getDeclaredField("mapOfQuese");
        field.setAccessible(true);
        ConcurrentHashMap<String, ConcurrentLinkedQueue> mapOfQuese  = (ConcurrentHashMap<String, ConcurrentLinkedQueue>)field.get(pool);

        assertEquals(null,mapOfQuese.get("An Actor 1"));

        actionToWaitFor1.handle(new ActorThreadPool(2),"", new PrivateState() {});
        actionToWaitFor2.handle(new ActorThreadPool(2),"", new PrivateState() {});

        assertEquals(true,mapOfQuese.get("An Actor 1").contains(actionOne));

        mapOfQuese.get("An Actor 1").poll();

        assertEquals(false,mapOfQuese.get("An Actor 1").contains(actionOne));

        assertEquals(true,mapOfQuese.containsKey("An Actor 1"));
        assertEquals(true,mapOfQuese.containsKey("An Actor 2"));
        assertEquals(true,mapOfQuese.get("An Actor 2").contains(actionToWaitFor1));
        assertEquals(false,mapOfQuese.get("An Actor 1").contains(actionToWaitFor1));


        // Assertion which required a correct actor thread pool from now on, unill now action was correct //
        // Checking that "then" method can handle empty actions to wait for list
        ActorThreadPool pool2 = new ActorThreadPool(4);

        assertEquals(false,actionStartExecutedTwo);
        assertEquals(false,actionContinuationExecutedTwo);

        pool2.submit(actionTwo,"An Actor 1",new PrivateState() {});

        CountDownLatch latch = new CountDownLatch(1);

        actionTwo.getResult().subscribe(() -> latch.countDown());

        pool2.start();

        try {
            latch.await();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            pool2.shutdown();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(true,actionStartExecutedTwo);
        assertEquals(true,actionContinuationExecutedTwo);
    }

    @Test(timeout=10000)
    public void testGeneralMethods() throws NoSuchFieldException, IllegalAccessException {
        // Creating test data
        Action<Boolean> action = new Action<Boolean>() {
            @Override
            protected void start() {
                complete(true);
                }
        };

        action.setActionName("Hello");

        ActorThreadPool pool = new ActorThreadPool(4);

        action.handle(pool, "An Actor 1", new PrivateState() {});

        // Initiating test
        assertEquals("Hello",action.getActionName());
        assertEquals(true,action.getResult().get());
    }
}