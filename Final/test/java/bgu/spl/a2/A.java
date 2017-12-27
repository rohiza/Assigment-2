package bgu.spl.a2;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class A {
    @Test
    public void test(){

    }

    public static ActorThreadPool initiateActionTest(Vector<Action<Boolean>> actions, Vector<PrivateState> actionsPrivateStates,
                                          Vector<String> actionsActorIds){
        ActorThreadPool pool;
        VersionMonitor verM = new VersionMonitor();

        pool = new ActorThreadPool(8);

        CountDownLatch latch = new CountDownLatch(actions.size());

        for (int i = 0; i < actions.size(); i++) {
            pool.submit(actions.get(i), actionsActorIds.get(i), actionsPrivateStates.get(i));
            actions.get(i).getResult().subscribe(() -> latch.countDown());
        }

        pool.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return pool;
    }

    // Initiating threads
    public static List<Thread> initiateThreads(int numThreads, Runnable exec){
        // Initializing
        List<Thread> threads = new ArrayList<>();

        // Creating threads
        for(int i = 0;i < numThreads;i++){
            threads.add(new Thread(exec));
        }

        // Starting threads
        for(int i = 0;i < threads.size();i++){
            threads.get(i).start();
        }

        // Waiting for all thread to execute the exec method
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return threads;
    }

    // Asserting all threads finished
    public static void assertAllThreadsFinished(List<Thread> threads){
        // Waiting for all threads to finish
        for(int i = 0;i < threads.size();i++){
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> Vector<T> createVectorFromArray(T[] array){
        Vector<T> vector = new Vector<>();

        for(int i = 0;i < array.length;i++){
            vector.add(array[i]);
        }

        return vector;
    }

    public static <T,V> boolean containsSame(List<T> list1,List<V> list2){
        for(T element : list1){
            if(!list2.contains(element)){
                return false;
            }
        }

        for(V element : list2){
            if(!list1.contains(element)){
                return false;
            }
        }

        return true;
    }

    public static void createPhase(Vector<Action> actions, Vector<PrivateState> actionsPrivateStates,
                                      Vector<String> actionsActorIds,Vector<Action<Boolean>> phasesActions,
                                   Vector<PrivateState> phasesPrivateStates,Vector<String> phasesActorsIds){
        Vector<Action<Boolean>> actionsForUse = new Vector<>();
        Vector<PrivateState> actionsForUsePrivateStates = new Vector<>();
        Vector<String> actionsForUseActorIds = new Vector<>();

        for(Action<Boolean> action : actions){
            actionsForUse.add(action);
        }

        for(PrivateState privateState : actionsPrivateStates){
            actionsForUsePrivateStates.add(privateState);
        }

        for(String id : actionsActorIds){
            actionsForUseActorIds.add(id);
        }

        Action<Boolean> phaseAction = new Action<Boolean>() {
            @Override
            protected void start() {
                CountDownLatch latch = new CountDownLatch(actionsForUse.size());

                for (int i = 0; i < actionsForUse.size(); i++) {
                    poolThreads.submit(actionsForUse.get(i), actionsForUseActorIds.get(i), actionsForUsePrivateStates.get(i));
                    actionsForUse.get(i).getResult().subscribe(() -> latch.countDown());
                }

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                complete(true);
            }
        };

        phasesActions.add(phaseAction);
        phasesPrivateStates.add(new PrivateState() {});
        phasesActorsIds.add("phases");

        actions.clear();
        actionsActorIds.clear();
        actionsPrivateStates.clear();
    }

    public static <T,V> HashMap<T,V> createHash(T[] keys,V[] values){
        HashMap<T,V> hash = new HashMap<>();

        if(keys.length != values.length){
            throw new ArrayIndexOutOfBoundsException();
        }

        for(int i = 0;i < keys.length;i++){
            hash.put(keys[i],values[i]);
        }

        return hash;
    }
}
