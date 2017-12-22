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
	public static ActorThreadPool actorThreadPool;
	protected static List<Computer> computers = new ArrayList<>();
    private static CountDownLatch latch;

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
		//TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
	}


	public static void main(String[] args) throws IOException {
		JsonParser praser = new JsonParser();
		FileReader fileReader = new FileReader(args[0]);
		JsonObject jsonObject = (JsonObject) praser.parse(fileReader);
		numofThreads = jsonObject.get("threads").getAsInt();
		JsonArray computerArray = jsonObject.getAsJsonArray("Computers");
		createComputers(computerArray);
		getPhases(jsonObject);
		for (Computer c : computers)
			c.toString();

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

	private static void getPhases(JsonObject object) throws InterruptedException {
		JsonArray Phase1 = object.getAsJsonArray("Phase 1");
		JsonArray Phase2 = object.getAsJsonArray("Phase 2");
		JsonArray Phase3 = object.getAsJsonArray("Phase 3");
		latch = new CountDownLatch(Phase1.size());
		getActionsPhases(Phase1);
		latch.await();
		getActionsPhases(Phase2);
		getActionsPhases(Phase3);
	}

	private static void getActionsPhases(JsonArray array) {
		for (int i = 0; i < array.size(); i++) {
			JsonObject obj = (JsonObject) array.get(i);
			String actionName = obj.get("Action").getAsString();
			Action<Boolean> openActor = new actionOpenActor();
			Action<?> actionToADD = null;
			if (actionName.equals("Open Course")) {
				String departmentName = obj.get("Department").getAsString();
				String courseName = obj.get("Course").getAsString();
				Integer spaceS = obj.get("Space").getAsInt();
				JsonArray a = (JsonArray) obj.getAsJsonArray("Prerequisites");
				LinkedList<String> pre = new LinkedList<>();
				for (int j = 0; j < a.size(); j++) {
					pre.add(a.get(j).getAsString());
				}
				actionToADD = new openNewCourse(departmentName, courseName, spaceS, pre);
				actorThreadPool.submit(actionToADD, departmentName, new DepartmentPrivateState());
			} else if (actionName.equals("Add Student")) {
				String departmentName = obj.get("Department").getAsString();
				String Name = obj.get("Student").getAsString();
				actionToADD = new addStudent(Name);
				actorThreadPool.submit(actionToADD, departmentName, new DepartmentPrivateState());
			} else if (actionName.equals("Participating In Course")) {
				String id = obj.get("Student").getAsString();
				String courseName = obj.get("Course").getAsString();
				JsonArray a = (JsonArray) obj.getAsJsonArray("Grade");
				Integer grade = (a.get(0).equals("-") ? -1 : Integer.parseInt(a.get(0).getAsString()));
				actionToADD = new ParticipiatingInCourse(id, grade);
				actorThreadPool.submit(actionToADD, courseName, new CoursePrivateState());
			}
			else if(actionName.equals("Unregister")) {
				String id = obj.get("Student").getAsString();
				String courseName = obj.get("Course").getAsString();
				actionToADD = new Unregister();
				actorThreadPool.submit(actionToADD, courseName, new CoursePrivateState());
			}
			else if(actionName.equals("Close Course")){
				String departmentName = obj.get("Department").getAsString();
				String courseName = obj.get("Course").getAsString();
				actionToADD = new closeAcourse();
				actorThreadPool.submit(actionToADD, departmentName, new DepartmentPrivateState());
			}
			else if(actionName.equals("Add Spaces")){
				String departmentName = obj.get("Department").getAsString();
				Integer number = Integer.parseInt(obj.get("Number").getAsString());
				actionToADD = new addSpaces();
				actorThreadPool.submit(actionToADD, departmentName, new DepartmentPrivateState());
			}
			else if(actionName.equals("Administrative Check")){
				String departmentName = obj.get("Department").getAsString();
				JsonArray a = (JsonArray) obj.getAsJsonArray("Students");
				LinkedList<String> Students = new LinkedList<>();
				for (int j = 0; j < a.size(); j++) {
					Students.add(a.get(j).getAsString());
				}
				String computerName = obj.get("Computer").getAsString();
				JsonArray b = (JsonArray) obj.getAsJsonArray("Conditions");
				LinkedList<String> conditions = new LinkedList<>();
				for (int j = 0; j < b.size(); j++) {
					conditions.add(b.get(j).getAsString());
				}
				actionToADD = new administrativeCheck();
				actorThreadPool.submit(actionToADD, departmentName, new DepartmentPrivateState());
			}
			else if(actionName.equals("Register With Preferences")){
				String id = obj.get("Student").getAsString();
				JsonArray a = (JsonArray) obj.getAsJsonArray("Preferences");
				LinkedList<String> preferences = new LinkedList<>();
				for (int j = 0; j < a.size(); j++) {
					preferences.add(a.get(j).getAsString());
				}
				JsonArray b = (JsonArray) obj.getAsJsonArray("Grade");
				LinkedList<String> grades = new LinkedList<>();
				for (int j = 0; j < b.size(); j++) {
					Integer number = Integer.parseInt(b.get(j).getAsString());
					grades.add(number);
				}
				actionToADD = new registerWithPreferences();
				actorThreadPool.submit(actionToADD, id, new StudentPrivateState());
			}
		}
	}
}



