package bgu.spl.a2;

import org.junit.Test;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import bgu.spl.a2.PrivateState;

import static org.junit.Assert.*;

public class ActorThreadPoolTest {
    @Test(timeout=10000)
    public void testWorkFlowBasic(){
        ActorThreadPool pool = new ActorThreadPool(2);

        Transmission trans = new Transmission(200,"B","A",
                "Bank 2");

        HashMap<String,Integer> testRecordsOne = new HashMap<>();
        HashMap<String,Integer> testRecordsTwo= new HashMap<>();

        testRecordsOne.put("A",3800);
        testRecordsTwo.put("B",4200);

        pool.start();

        pool.submit(trans,"Bank 1",new BankState("Bank 1"));

        CountDownLatch latch = new CountDownLatch(1);
        trans.getResult().subscribe(() -> latch.countDown());

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

        assertEquals(testRecordsOne,((BankState)pool.getPrivateState("Bank 1")).records);
        assertEquals(testRecordsTwo,((BankState)pool.getPrivateState("Bank 2")).records);
    }

    @Test(timeout=10000)
    public void testWorkFlowIntermediate(){
        initiateTestByLevel(2,10);
    }

    @Test(timeout=13000)
    public void testWorkFlowAdvance(){
        initiateTestByLevel(10,100);
        initiateAdvanceTransmission(10,20000);
    }

    public void initiateTestByLevel(Integer numOfThreads,Integer numOfSends){
        ActorThreadPool pool = new ActorThreadPool(numOfThreads);
        Vector<HashMap<String,Integer>> recordsOfBanks = new Vector<>();
        int amountOfSendes = numOfSends;

        CountDownLatch latch = new CountDownLatch(amountOfSendes * 20);

        for(int i = 0;i < 40;i++){
            recordsOfBanks.add(new HashMap<>());
        }

        for(int i = 0;i < 20;i++){
            recordsOfBanks.get(i).put("A",4000 - 200*amountOfSendes);
        }

        for(int i = 20;i < 40;i++){
            recordsOfBanks.get(i).put("B",4000 + 200*amountOfSendes);
        }


        initiateTransmissions(pool,latch);

        pool.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int j = 0;j < amountOfSendes - 1;j++) {
            initiateTransmissions(pool,latch);
        }

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

        for(int i = 0;i < 40;i++){
            assertEquals(recordsOfBanks.get(i),((BankState)pool.getPrivateState("Bank " + i)).records);
        }
    }

    public void initiateAdvanceTransmission(Integer numOfThreads,Integer numOfSends){
        ActorThreadPool pool = new ActorThreadPool(numOfThreads);
        Vector<HashMap<String,Integer>> recordsOfBanks = new Vector<>();

        CountDownLatch latch = new CountDownLatch(numOfSends);

        for(int i = 0;i < numOfSends + 1;i++){
            recordsOfBanks.add(new HashMap<>());
        }

        for(int i = 0;i < numOfSends;i++){
            recordsOfBanks.get(i).put("A",4000 - 200);
        }

        recordsOfBanks.get(numOfSends).put("B",4000 + 200*numOfSends);

        pool.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < numOfSends; i++) {
            Transmission trans = new Transmission(200, "B", "A",
                    "Bank " + numOfSends);
            trans.getResult().subscribe(() -> latch.countDown());
            pool.submit(trans, "Bank " + i, new BankState("Bank " + i));
        }

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

        for(int i = 0;i < numOfSends + 1;i++){
            assertEquals(recordsOfBanks.get(i),((BankState)pool.getPrivateState("Bank " + i)).records);
        }
    }

    public void initiateTransmissions(ActorThreadPool pool,CountDownLatch latch){
        for (int i = 0; i < 20; i++) {
            Integer receiverBankNumber = (i + 20);

            Transmission trans = new Transmission(200, "B", "A",
                    "Bank " + receiverBankNumber);
            trans.getResult().subscribe(() -> latch.countDown());
            pool.submit(trans, "Bank " + i, new BankState("Bank " + i));
        }
    }

    public  class Transmission extends Action<String>{
        int amount;
        String sender;
        String receiver;
        String receiverBank;

        public Transmission(int theAmount, String theReceiver,String theSender,
                            String theReceiverBank){
            this.amount = theAmount;
            this.receiver = theReceiver;
            this.sender = theSender;
            this.receiverBank = theReceiverBank;
        }

        @Override
        protected void start(){
            Vector<Action<Boolean>> actions = new Vector<>();
            Action<Boolean> confAction = new Confirmation(amount,receiver);
            actions.add(confAction);

            sendMessage(confAction,receiverBank,new BankState(this.receiverBank));

            then(actions,()->{
               if(actions.get(0).getResult().get()){
                   ((BankState) actorState).amountSended(sender,amount);
                   complete("transmission succeeded");
               }
               else{
                   complete("transmission failed");
               }
            });
         }

    }

    public static class Confirmation extends Action<Boolean>{
        int amount;
        String receiver;

        public Confirmation(int theAmount, String theReceiver){
            this.amount = theAmount;
            this.receiver = theReceiver;
        }

        @Override
        protected void start(){
            ((BankState) actorState).amountReceived(receiver,amount);
            complete(true);
        }

    }

    public static class BankState extends PrivateState{
        public HashMap<String,Integer> records = new HashMap<>();
        public String name;

        public BankState(String name){
            this.name = name;
        }

        public void amountSended(String theSender,Integer theAmount){
            if(!records.containsKey(theSender)){
                records.put(theSender,4000);
            }

            records.put(theSender,records.get(theSender) - theAmount);
        }

        public void amountReceived(String theReceiver,Integer theAmount){
            if(!records.containsKey(theReceiver)){
                records.put(theReceiver,4000);
            }

            records.put(theReceiver,records.get(theReceiver) + theAmount);
        }

    }
}