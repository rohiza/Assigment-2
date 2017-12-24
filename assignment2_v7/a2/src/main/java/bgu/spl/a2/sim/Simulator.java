/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
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
    public static ActorThreadPool actorThreadPool;
    protected static List<Computer> computers = new ArrayList<>();

    /**
     * Begin the simulation Should not be called before attachActorThreadPool()
     */
    public static void start() {
        //TODO: replace method body with real implementation
        throw new UnsupportedOperationException("Not Implemented Yet.");
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
        return (HashMap<String, PrivateState>) actorThreadPool.getActors();
    }

    public static void main(String[] args) throws IOException {

        JsonParser praser = new JsonParser();
        FileReader fileReader = new FileReader(args[0]);
        JsonObject jsonObject = (JsonObject) praser.parse(fileReader);
        numofThreads = jsonObject.get("threads").getAsInt();
        JsonArray computerArray = jsonObject.getAsJsonArray("Computers");
        createComputers(computerArray);
        actorThreadPool = new ActorThreadPool(numofThreads);
        actorThreadPool.start();
        getPhases(jsonObject);
    }

    private static void createComputers(JsonArray array) throws IOException {
        for (JsonElement jsonElement : array) {
            computers.add(createComp(jsonElement));
        }
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

    private static void getPhases(JsonObject object) {
        JsonArray Phase1 = object.getAsJsonArray("Phase 1");
        JsonArray Phase2 = object.getAsJsonArray("Phase 2");
        JsonArray Phase3 = object.getAsJsonArray("Phase 3");
        latch1 = new CountDownLatch(Phase1.size());
        getActionsPhases(Phase1);
        try {
            latch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        latch2 = new CountDownLatch(Phase2.size());
        getActionsPhases(Phase2);
        try {
            latch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getActionsPhases(Phase3);
        actorThreadPool.getActors().forEach((k,v) ->{
            System.out.println("The actorId : " +  k);
            if( v instanceof  CoursePrivateState)
                ((CoursePrivateState) v).getRegStudents().forEach(s -> System.out.println("Student name is " + s));
            else if (v instanceof StudentPrivateState)
                ((StudentPrivateState) v).getGrades().forEach((a,b) -> {
                    System.out.println(a + " " + b);
                });
            if(v instanceof  DepartmentPrivateState)
                ((DepartmentPrivateState) v).getStudentList().forEach(s -> System.out.println("Student " + s));

            v.getLogger().forEach(s -> System.out.println("The Action in private state  " + s ));
        });
    }

    private static void getActionsPhases(JsonArray array) {
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = (JsonObject) array.get(i);
            String actionName = obj.get("Action").getAsString();
            if (actionName.equals("Open Course")) {
                opencourse(obj, null);
            }
            else if (actionName.equals("Add Student")) {
                addstudent(obj, null);
            }
            else if (actionName.equals("Participate In Course")) {
                participate(obj, null);
            }
            else if(actionName.equals("Unregister")) {
                unregister(obj, null);
            }
            else if(actionName.equals("Close Course")){
                closecourse(obj, null);
            }
            else if(actionName.equals("Add Spaces")){
                addspaces(obj, null);
            }
            else if(actionName.equals("Administrative Check")){
                admincheck(obj, null);
            }
            else if(actionName.equals("Register With Preferences")){
                registerPre(obj, null);
            }
        }
    }

    private static void opencourse(JsonObject o, Action action){
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
        latchDown(action);
    }

    private static void addstudent(JsonObject o, Action action){
        String departmentName = o.get("Department").getAsString();
        String id = o.get("Student").getAsString();
        action = new addStudent(id);
        actorThreadPool.submit(action, departmentName, new DepartmentPrivateState());
        latchDown(action);
    }

    private static void participate(JsonObject o, Action action){
        String id = o.get("Student").getAsString();
        String courseName = o.get("Course").getAsString();
        JsonArray a = o.getAsJsonArray("Grade");
        Integer grade = (a.get(0).getAsString().equals("-") ? -1 : Integer.parseInt(a.get(0).getAsString()));
        action = new ParticipiatingInCourse(id, courseName,grade);
        actorThreadPool.submit(action, courseName, new CoursePrivateState());
        latchDown(action);
    }

    private static void unregister(JsonObject o, Action action){
        String id = o.get("Student").getAsString();
        String courseName = o.get("Course").getAsString();
        action = new Unregister(id, courseName);
        actorThreadPool.submit(action, courseName, new CoursePrivateState());
        latchDown(action);
    }

    private static void closecourse(JsonObject o, Action action){
        String departmentName = o.get("Department").getAsString();
        String courseName = o.get("Course").getAsString();
        action = new closeAcourse(courseName);
        actorThreadPool.submit(action, departmentName, new DepartmentPrivateState());
        latchDown(action);
    }

    private static void addspaces(JsonObject o, Action action){
        String courseName = o.get("Course").getAsString();
        Integer number = Integer.parseInt(o.get("Number").getAsString());
        action = new addSpaces(number);
        actorThreadPool.submit(action, courseName, new CoursePrivateState());
        latchDown(action);
    }

    private static void admincheck(JsonObject o, Action action){ //todo: mutex
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
        action = new checkAdmin();
        actorThreadPool.submit(action, departmentName, new DepartmentPrivateState());
        latchDown(action);
    }

    private static void registerPre(JsonObject o, Action action){
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
        latchDown(action);
    }

    private static void latchDown(Action action){
        if(latch1.getCount() > 0){
            action.getResult().subscribe(()-> latch1.countDown());
        }
        if(latch2 != null && latch2.getCount() > 0){
            action.getResult().subscribe(()-> latch2.countDown());
        }
    }
}



