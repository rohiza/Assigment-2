        /*
         * To change this license header, choose License Headers in Project Properties.
         * To change this template file, choose Tools | Templates
         * and open the template in the editor.
         */
        package bgu.spl.a2.sim;
        import java.io.*;
        import java.util.*;
        import java.util.concurrent.ConcurrentHashMap;
        import java.util.concurrent.CountDownLatch;

        import bgu.spl.a2.Action;
        import bgu.spl.a2.ActorThreadPool;
        import bgu.spl.a2.PrivateState;
        import bgu.spl.a2.sim.actions.*;
        import bgu.spl.a2.sim.privateStates.CoursePrivateState;
        import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
        import bgu.spl.a2.sim.privateStates.StudentPrivateState;
        import com.google.gson.*;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
    private static int numofThreads;
    private static CountDownLatch latch1 = null;
    private static CountDownLatch latch2 = null;
    private static CountDownLatch latch3 = null;
    public static ActorThreadPool actorThreadPool;
    protected static ArrayList<Computer> computers = new ArrayList<>();
    public static Warehouse currWarehouse;
    private static JsonObject jsonO;
    private static JsonArray phase1;
    private static JsonArray phase2;
    private static JsonArray phase3;
    /**
     * Begin the simulation Should not be called before attachActorThreadPool()
     */
    public static void start() {
        latch1 = new CountDownLatch(phase1.size());
        getActionsPhases(phase1,latch1);
        try {
  //          System.out.println(" latch 1 is awaiting.. "   + latch1.getCount() + "\n" );
            latch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        latch2 = new CountDownLatch(phase2.size());
        getActionsPhases(phase2,latch2);
        try {

      //      System.out.println(" latch 2 is awaiting.. " + latch2.getCount());
            latch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      latch3 = new CountDownLatch(phase3.size());
        getActionsPhases(phase3,latch3);
       try {
    //        System.out.println(" latch 3 is awaiting.. " + latch3.getCount());
            latch3.await();
     //       System.out.println("i finish all the phases Stuck???");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       // Test();
        }


    /**
     * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
     *
     * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
     */
    public static void attachActorThreadPool(ActorThreadPool myActorThreadPool) {
        actorThreadPool = myActorThreadPool;
    }

    /**
     * shut down the simulation
     * returns list of private states
     */
    public static HashMap<String, PrivateState> end() {
        try {
            actorThreadPool.shutdown();
        } catch (InterruptedException e) {
        }
        return new HashMap<>(actorThreadPool.getActors());
    }

    public static void main(String[] args) throws IOException {
        JsonParser praser = new JsonParser();
        FileReader fileReader = new FileReader(args[0]);
        JsonObject jsonObject = (JsonObject) praser.parse(fileReader);
        numofThreads = jsonObject.get("threads").getAsInt();
        JsonArray computerArray = jsonObject.getAsJsonArray("Computers");
        createComputers(computerArray);
        attachActorThreadPool(actorThreadPool);
        actorThreadPool = new ActorThreadPool(numofThreads);
        jsonO = jsonObject;
        getPhases(jsonO);
        actorThreadPool.start();
        start();
  //      System.out.println("Reading the map..");
        HashMap<String, PrivateState> actorsState = Simulator.end();

        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            fout = new FileOutputStream("result.ser");
            oos = new ObjectOutputStream(fout);
            oos.writeObject(actorsState);

        } catch (Exception e) {
        } finally {
            try {
                if (fout != null)
                    fout.close();
                if (oos != null)
                    oos.close();
            } catch (Exception e) {
            }
        }
    }

    public static CountDownLatch getLatch1() {
        return latch1;
    }

    public static CountDownLatch getLatch2() {
        return latch2;
    }

    public static CountDownLatch getLatch3() {
        return latch3;
    }

    private static void createComputers(JsonArray array) throws IOException {
        for (JsonElement jsonElement : array) {
            computers.add(createComp(jsonElement));
        }
        currWarehouse = new Warehouse(computers);
    }

    private static Computer createComp(JsonElement element) throws IOException {
        Computer newCompuer = null;
        try {
            newCompuer = new Computer(element.getAsJsonObject().get("Type").getAsString());
            newCompuer.setSuccessSig(element.getAsJsonObject().get("Sig Success").getAsLong());
            newCompuer.setFailSig(element.getAsJsonObject().get("Sig Fail").getAsLong());
        } catch (Exception e) {
        }
        return newCompuer;
    }

    public static void getPhases(JsonObject object) {
        phase1 = object.getAsJsonArray("Phase 1");
        phase2 = object.getAsJsonArray("Phase 2");
        phase3 = object.getAsJsonArray("Phase 3");
    }

    private static void getActionsPhases(JsonArray array,CountDownLatch latch) {
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = (JsonObject) array.get(i);
            String actionName = obj.get("Action").getAsString();
            if (actionName.equals("Open Course")) {
                opencourse(obj, null,latch);
            }
            else if (actionName.equals("Add Student")) {
                addstudent(obj, null,latch);
            }
            else if (actionName.equals("Participate In Course")) {
                participate(obj, null,latch);
            }
            else if(actionName.equals("Unregister")) {
                unregister(obj, null,latch);
            }
            else if(actionName.equals("Close Course")){
                closecourse(obj, null,latch);
            }
            else if(actionName.equals("Add Spaces")){
                addspaces(obj, null,latch);
            }
            else if(actionName.equals("Administrative Check")){
                admincheck(obj, null,latch);
            }
            else if(actionName.equals("Register With Preferences")){
                registerPre(obj, null,latch);
            }
        }
    }

    private static void opencourse(JsonObject o, Action action,CountDownLatch latch){
        String departmentName = o.get("Department").getAsString();
        String courseName = o.get("Course").getAsString();
        Integer spaceS = o.get("Space").getAsInt();
        JsonArray a = o.getAsJsonArray("Prerequisites");
        LinkedList<String> pre = new LinkedList<>();
        for (int j = 0; j < a.size(); j++) {
            pre.add(a.get(j).getAsString());
        }
        action = new openNewCourse(departmentName, courseName, spaceS, pre);
        actorThreadPool.submit(action, departmentName, new DepartmentPrivateState());
        latchDown(action,latch);
      //  System.out.println("open course did latchdown");
    }

    private static void addstudent(JsonObject o, Action action,CountDownLatch latch){
        String departmentName = o.get("Department").getAsString();
        String id = o.get("Student").getAsString();
        action = new addStudent(id);
        actorThreadPool.submit(action, departmentName, new DepartmentPrivateState());
        latchDown(action,latch);
      //  System.out.println("add student did latchdown");
    }

    private static void participate(JsonObject o, Action action,CountDownLatch latch){
        String id = o.get("Student").getAsString();
        String courseName = o.get("Course").getAsString();
        JsonArray a = o.getAsJsonArray("Grade");
        Integer grade = (a.get(0).getAsString().equals("-") ? -1 : Integer.parseInt(a.get(0).getAsString()));
        action = new ParticipiatingInCourse(id, courseName,grade);
        actorThreadPool.submit(action, courseName, new CoursePrivateState());
        latchDown(action,latch);
      //  System.out.println("participate did latchdown");
    }

    private static void unregister(JsonObject o, Action action,CountDownLatch latch){
        String id = o.get("Student").getAsString();
        String courseName = o.get("Course").getAsString();
        action = new Unregister(id, courseName);
        actorThreadPool.submit(action, courseName, new CoursePrivateState());
        latchDown(action,latch);
       // System.out.println("unregister did latchdown");
    }

    private static void closecourse(JsonObject o, Action action,CountDownLatch latch){
        String departmentName = o.get("Department").getAsString();
        String courseName = o.get("Course").getAsString();
        action = new closeAcourse(courseName);
        actorThreadPool.submit(action, departmentName, new DepartmentPrivateState());
        latchDown(action,latch);
       // System.out.println("its work");
    }

    private static void addspaces(JsonObject o, Action action,CountDownLatch latch){
        String courseName = o.get("Course").getAsString();
        Integer number = Integer.parseInt(o.get("Number").getAsString());
        action = new addSpaces(number);
        actorThreadPool.submit(action, courseName, new CoursePrivateState());
        latchDown(action,latch);
     ////   System.out.println("add spaces did latchdown");
    }

    private static void admincheck(JsonObject o, Action action,CountDownLatch latch){ //todo: mutex
        String departmentName = o.get("Department").getAsString();
        JsonArray a =  o.getAsJsonArray("Students");
        LinkedList<String> studentsId = new LinkedList<>();
        for (int j = 0; j < a.size(); j++) {
            studentsId.add(a.get(j).getAsString());
        }
        String computerName = o.get("Computer").getAsString();
        JsonArray b = o.getAsJsonArray("Conditions");
        LinkedList<String> conditions = new LinkedList<>();
        for (int j = 0; j < b.size(); j++) {
            conditions.add(b.get(j).getAsString());
        }
        action = new checkAdmin(computerName, studentsId, conditions,currWarehouse);
        actorThreadPool.submit(action, departmentName, new DepartmentPrivateState());
        latchDown(action,latch);
       // System.out.println("check admin did latchdown");
    }

    private static void registerPre(JsonObject o, Action action,CountDownLatch latch){
        String id = o.get("Student").getAsString();
        JsonArray a = o.getAsJsonArray("Preferences");
        LinkedList<String> preferences = new LinkedList<>();
        for (int j = 0; j < a.size(); j++) {
            preferences.add(a.get(j).getAsString());
        }
        JsonArray b = o.getAsJsonArray("Grade");
        LinkedList<Integer> grades = new LinkedList<>();
        for (int j = 0; j < b.size(); j++) {
            Integer number = Integer.parseInt(b.get(j).getAsString());
            grades.add(number);
        }
        action = new registerWithPreferences(id, preferences, grades);
        actorThreadPool.submit(action, id, actorThreadPool.getPrivateState(id));
        latchDown(action,latch);

    }
    private static void latchDown(Action action,CountDownLatch latch){
        action.getResult().subscribe(()->{
    /*        if(latch == latch1)
                System.out.println("We are in latch 1 :" +"\n" + action.getActionName() + " Action number : " + latch.getCount() + " Success latch down" + "\n"  );
            if(latch == latch2)
                System.out.println("We are in latch 2 : " +"\n" + action.getActionName() + " Action number  : " + latch.getCount() + " Success latch down" + "\n"  );

            if(latch == latch3)
            System.out.println("We are in latch 3 : " +"\n" + action.getActionName() + " Action number  : " + latch.getCount() + " Success latch down" + "\n"  );
      */      latch.countDown();
        });

    }

    public static void Test(){
        actorThreadPool.getActors().forEach((k,v) ->{
            System.out.println();
            System.out.println("The actorId : " +  k);
            if( v instanceof  CoursePrivateState)
                ((CoursePrivateState) v).getRegStudents().forEach(s -> System.out.println("Student name is " + s));
            else if (v instanceof StudentPrivateState)
                ((StudentPrivateState) v).getGrades().forEach((a,b) -> {
                    System.out.println("Course name is :" + a  + " The Grade in course is " + b);
                    System.out.println("Student signture is : " + ((StudentPrivateState) v).getSignature());
                });
            if(v instanceof  DepartmentPrivateState) {
                ((DepartmentPrivateState) v).getStudentList().forEach(s -> System.out.println("Student " + s));
                ((DepartmentPrivateState) v).getCourseList().forEach(s -> System.out.println("Course  " + s));
            }
            v.getLogger().forEach(s -> System.out.println("The Action in private state  " + s ));
        });
    }

}
