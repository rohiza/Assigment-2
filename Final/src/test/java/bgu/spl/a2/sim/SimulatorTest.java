package bgu.spl.a2.sim;

import bgu.spl.a2.A;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.Assert.*;

public class SimulatorTest {
    @Test(timeout=10000)
    public void testOpenCourse() {

        HashMap<String,PrivateState> testResult = executeTest("openCourse");

        assertEquals(true,testResult.containsKey("Dep1"));
        assertEquals(true,testResult.containsKey("Dep2"));
        assertEquals(true,testResult.containsKey("Dep3"));
        assertEquals(true,testResult.containsKey("Dep4"));

        assertEquals(true,testResult.containsKey("Cou1"));
        assertEquals(true,testResult.containsKey("Cou2"));
        assertEquals(true,testResult.containsKey("Cou3"));
        assertEquals(true,testResult.containsKey("Cou4"));
        assertEquals(true,testResult.containsKey("Cou5"));
        assertEquals(true,testResult.containsKey("Cou6"));

        assertEquals(A.createVectorFromArray(new String[] {}),((DepartmentPrivateState)testResult.get("Dep1")).getStudentList());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"Cou1","Cou2","Cou3","Cou4"}),
                ((DepartmentPrivateState)testResult.get(
                        "Dep1")).getCourseList()));

        assertEquals(A.createVectorFromArray(new String[] {}),((DepartmentPrivateState)testResult.get("Dep2")).getStudentList());
        assertEquals(A.createVectorFromArray(new String[] {"Cou5"}),((DepartmentPrivateState)testResult.get("Dep2")).getCourseList());

        assertEquals(A.createVectorFromArray(new String[] {}),((DepartmentPrivateState)testResult.get("Dep3")).getStudentList());
        assertEquals(A.createVectorFromArray(new String[] {"Cou6"}),((DepartmentPrivateState)testResult.get("Dep3")).getCourseList());

        assertEquals(A.createVectorFromArray(new String[] {}),((DepartmentPrivateState)testResult.get("Dep4")).getStudentList());
        assertEquals(A.createVectorFromArray(new String[] {"Cou7"}),((DepartmentPrivateState)testResult.get("Dep4")).getCourseList());

        assertEquals(A.createVectorFromArray(new String[] {}),((CoursePrivateState)testResult.get("Cou1")).getPrequisites());
        assertEquals(A.createVectorFromArray(new String[] {}),((CoursePrivateState)testResult.get("Cou1")).getRegStudents());
        assertEquals(20,(long)((CoursePrivateState)testResult.get("Cou1")).getAvailableSpots());

        assertEquals(A.createVectorFromArray(new String[] {"A","B"}),((CoursePrivateState)testResult.get("Cou2")).getPrequisites());
        assertEquals(A.createVectorFromArray(new String[] {}),((CoursePrivateState)testResult.get("Cou2")).getRegStudents());
        assertEquals(100,(long)((CoursePrivateState)testResult.get("Cou2")).getAvailableSpots());

        assertEquals(A.createVectorFromArray(new String[] {"C"}),((CoursePrivateState)testResult.get("Cou3")).getPrequisites());
        assertEquals(A.createVectorFromArray(new String[] {}),((CoursePrivateState)testResult.get("Cou3")).getRegStudents());
        assertEquals(0,(long)((CoursePrivateState)testResult.get("Cou3")).getAvailableSpots());

        assertEquals(A.createVectorFromArray(new String[] {"D","G","J"}),((CoursePrivateState)testResult.get("Cou4")).getPrequisites());
        assertEquals(A.createVectorFromArray(new String[] {}),((CoursePrivateState)testResult.get("Cou4")).getRegStudents());
        assertEquals(10,(long)((CoursePrivateState)testResult.get("Cou4")).getAvailableSpots());

        assertEquals(A.createVectorFromArray(new String[] {"1"}),((CoursePrivateState)testResult.get("Cou5")).getPrequisites());
        assertEquals(A.createVectorFromArray(new String[] {}),((CoursePrivateState)testResult.get("Cou5")).getRegStudents());
        assertEquals(15,(long)((CoursePrivateState)testResult.get("Cou5")).getAvailableSpots());

        assertEquals(A.createVectorFromArray(new String[] {"2","3"}),((CoursePrivateState)testResult.get("Cou6")).getPrequisites());
        assertEquals(A.createVectorFromArray(new String[] {}),((CoursePrivateState)testResult.get("Cou6")).getRegStudents());
        assertEquals(18,(long)((CoursePrivateState)testResult.get("Cou6")).getAvailableSpots());
    }

    @Test(timeout=10000)
    public void testAddStudent() {
        HashMap<String,PrivateState> testResult = executeTest("addStudent");

        assertEquals(true,((DepartmentPrivateState)testResult.get("A")).getStudentList().contains("1"));
        assertEquals(true,((DepartmentPrivateState)testResult.get("A")).getStudentList().contains("2"));
        assertEquals(false,((DepartmentPrivateState)testResult.get("A")).getStudentList().contains("3"));
        assertEquals(false,((DepartmentPrivateState)testResult.get("A")).getStudentList().contains("4"));
        assertEquals(false,((DepartmentPrivateState)testResult.get("A")).getStudentList().contains("5"));

        assertEquals(false,((DepartmentPrivateState)testResult.get("B")).getStudentList().contains("1"));
        assertEquals(false,((DepartmentPrivateState)testResult.get("B")).getStudentList().contains("2"));
        assertEquals(true,((DepartmentPrivateState)testResult.get("B")).getStudentList().contains("3"));
        assertEquals(true,((DepartmentPrivateState)testResult.get("B")).getStudentList().contains("4"));
        assertEquals(true,((DepartmentPrivateState)testResult.get("B")).getStudentList().contains("5"));

        assertEquals(true,testResult.containsKey("1"));
        assertEquals(true,testResult.containsKey("2"));
        assertEquals(true,testResult.containsKey("3"));
        assertEquals(true,testResult.containsKey("4"));
        assertEquals(true,testResult.containsKey("5"));
    }

    @Test(timeout=10000)
    public void testCheckAdministrativeObligations() {
        HashMap<String,PrivateState> testResult = executeTest("CheckAdministrativeObligations");

        assertEquals(-1,((StudentPrivateState)testResult.get("1")).getSignature());
        assertEquals(-1,((StudentPrivateState)testResult.get("2")).getSignature());
        assertEquals(1,((StudentPrivateState)testResult.get("3")).getSignature());

        assertEquals(-1,((StudentPrivateState)testResult.get("4")).getSignature());
        assertEquals(5,((StudentPrivateState)testResult.get("5")).getSignature());
        assertEquals(-5,((StudentPrivateState)testResult.get("6")).getSignature());
    }

    @Test(timeout=10000)
    public void testCloseCourse()
    {
        HashMap<String,PrivateState> testResult = executeTest("CloseCourse");

        assertEquals(A.createHash(new String[] {"D"},new Integer[] {10}),
                ((StudentPrivateState)testResult.get("1")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("2")).getGrades());

        assertEquals(A.createHash(new String[] {"D"},new Integer[] {30}),
                ((StudentPrivateState)testResult.get("3")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("4")).getGrades());

        assertEquals(-1,(long)
                ((CoursePrivateState)testResult.get("A")).getAvailableSpots());
        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("A")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {}),
                ((CoursePrivateState)testResult.get("A")).getRegStudents()));

        assertEquals(-1,(long)
                ((CoursePrivateState)testResult.get("B")).getAvailableSpots());
        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("B")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {}),
                ((CoursePrivateState)testResult.get("B")).getRegStudents()));

        assertEquals(-1,(long)
                ((CoursePrivateState)testResult.get("C")).getAvailableSpots());
        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("C")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {}),
                ((CoursePrivateState)testResult.get("C")).getRegStudents()));

        assertEquals(8,(long)
                ((CoursePrivateState)testResult.get("D")).getAvailableSpots());
        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("D")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","3"}),
                ((CoursePrivateState)testResult.get("D")).getRegStudents()));


        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {}),
                ((DepartmentPrivateState)testResult.get("Dep1")).getCourseList()));
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","2","3","4"}),
                ((DepartmentPrivateState)testResult.get("Dep1")).getStudentList()));

        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"D"}),
                ((DepartmentPrivateState)testResult.get("Dep2")).getCourseList()));
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"2"}),
                ((DepartmentPrivateState)testResult.get("Dep2")).getStudentList()));
    }

    @Test(timeout=10000)
    public void testOpenNewPlaces() {
        HashMap<String, PrivateState> testResult = executeTest("OpenNewPlacesInCourse");

        assertEquals(90, (long) ((CoursePrivateState) testResult.get("Cou1")).getAvailableSpots());
        assertEquals(115, (long) ((CoursePrivateState) testResult.get("Cou2")).getAvailableSpots());
        assertEquals(20, (long) ((CoursePrivateState) testResult.get("Cou3")).getAvailableSpots());
        assertEquals(-1, (long) ((CoursePrivateState) testResult.get("Cou5")).getAvailableSpots());
    }


    @Test(timeout=10000)
    public void testParticipateBasic() {
        HashMap<String, PrivateState> testResult = executeTest("ParticipateBasic");

        assertEquals(A.createHash(new String[] {"A","B","C","D"},new Integer[] {90,-1,20,10}),
                ((StudentPrivateState)testResult.get("1")).getGrades());

        assertEquals(A.createHash(new String[] {"A"},new Integer[] {45}),
                ((StudentPrivateState)testResult.get("2")).getGrades());

        assertEquals(A.createHash(new String[] {"A","B","C","D"},new Integer[] {60,40,30,30}),
                ((StudentPrivateState)testResult.get("3")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("4")).getGrades());

        assertEquals(7,(long)
                ((CoursePrivateState)testResult.get("A")).getAvailableSpots());
        assertEquals(3,(long)
                ((CoursePrivateState)testResult.get("A")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","2","3"}),
                ((CoursePrivateState)testResult.get("A")).getRegStudents()));

        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("B")).getAvailableSpots());
        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("B")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","3"}),
                ((CoursePrivateState)testResult.get("B")).getRegStudents()));

        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("C")).getAvailableSpots());
        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("C")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","3"}),
                ((CoursePrivateState)testResult.get("C")).getRegStudents()));

        assertEquals(1,(long)
                ((CoursePrivateState)testResult.get("D")).getAvailableSpots());
        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("D")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","3"}),
                ((CoursePrivateState)testResult.get("D")).getRegStudents()));
    }

    @Test(timeout=10000)
    public void testUnRegisterBasic() {
        HashMap<String, PrivateState> testResult = executeTest("UnRegisterBasic");

        assertEquals(A.createHash(new String[] {"A"},new Integer[] {90}),
                ((StudentPrivateState)testResult.get("1")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("2")).getGrades());

        assertEquals(A.createHash(new String[] {"B"},new Integer[] {40}),
                ((StudentPrivateState)testResult.get("3")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("4")).getGrades());

        assertEquals(9,(long)
                ((CoursePrivateState)testResult.get("A")).getAvailableSpots());
        assertEquals(1,(long)
                ((CoursePrivateState)testResult.get("A")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1"}),
                ((CoursePrivateState)testResult.get("A")).getRegStudents()));

        assertEquals(1,(long)
                ((CoursePrivateState)testResult.get("B")).getAvailableSpots());
        assertEquals(1,(long)
                ((CoursePrivateState)testResult.get("B")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"3"}),
                ((CoursePrivateState)testResult.get("B")).getRegStudents()));

        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("C")).getAvailableSpots());
        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("C")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {}),
                ((CoursePrivateState)testResult.get("C")).getRegStudents()));

        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("D")).getAvailableSpots());
        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("D")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {}),
                ((CoursePrivateState)testResult.get("D")).getRegStudents()));

    }

    @Test(timeout=10000)
    public void testParticipateAdvance() {
        HashMap<String,PrivateState> testResult = executeTest("ParticipateAdvance");

        assertEquals(A.createHash(new String[] {"A","B","C","D"},new Integer[] {90,-1,20,10}),
                ((StudentPrivateState)testResult.get("1")).getGrades());

        assertEquals(A.createHash(new String[] {"A"},new Integer[] {45}),
                ((StudentPrivateState)testResult.get("2")).getGrades());

        // Note that this is an advance case
        assertEquals(A.createHash(new String[] {"A","B","C","D"},new Integer[] {60,40,30,30}),
                ((StudentPrivateState)testResult.get("3")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("4")).getGrades());

        assertEquals(7,(long)
                ((CoursePrivateState)testResult.get("A")).getAvailableSpots());
        assertEquals(3,(long)
                ((CoursePrivateState)testResult.get("A")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","2","3"}),
                ((CoursePrivateState)testResult.get("A")).getRegStudents()));

        assertEquals(3,(long)
                ((CoursePrivateState)testResult.get("B")).getAvailableSpots());
        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("B")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","3"}),
                ((CoursePrivateState)testResult.get("B")).getRegStudents()));

        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("C")).getAvailableSpots());
        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("C")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","3"}),
                ((CoursePrivateState)testResult.get("C")).getRegStudents()));

        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("D")).getAvailableSpots());
        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("D")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","3"}),
                ((CoursePrivateState)testResult.get("D")).getRegStudents()));
    }

    @Test(timeout=10000)
    public void testRegWithPref() {
        HashMap<String,PrivateState> testResult = executeTest("RegWithPref");

        assertEquals(A.createHash(new String[] {"A","B"},new Integer[] {22,120}),
                ((StudentPrivateState)testResult.get("1")).getGrades());

        assertEquals(A.createHash(new String[] {"A"},new Integer[] {11}),
                ((StudentPrivateState)testResult.get("2")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("3")).getGrades());

        assertEquals(8,(long)
                ((CoursePrivateState)testResult.get("A")).getAvailableSpots());
        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("A")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","2"}),
                ((CoursePrivateState)testResult.get("A")).getRegStudents()));

        assertEquals(4,(long)
                ((CoursePrivateState)testResult.get("B")).getAvailableSpots());
        assertEquals(1,(long)
                ((CoursePrivateState)testResult.get("B")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1"}),
                ((CoursePrivateState)testResult.get("B")).getRegStudents()));

        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("C")).getAvailableSpots());
        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("C")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {}),
                ((CoursePrivateState)testResult.get("C")).getRegStudents()));

        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("D")).getAvailableSpots());
        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("D")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {}),
                ((CoursePrivateState)testResult.get("D")).getRegStudents()));
    }

    @Test(timeout=10000)
    public void testUnRegisterAdvance() {
        HashMap<String,PrivateState> testResult = executeTest("UnregisterAdvance");

        assertEquals(A.createHash(new String[] {"B","D"},new Integer[] {-1,10}),
                ((StudentPrivateState)testResult.get("1")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("2")).getGrades());

        assertEquals(A.createHash(new String[] {"A","B","D"},new Integer[] {60,40,30}),
                ((StudentPrivateState)testResult.get("3")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("4")).getGrades());

        assertEquals(9,(long)
                ((CoursePrivateState)testResult.get("A")).getAvailableSpots());
        assertEquals(1,(long)
                ((CoursePrivateState)testResult.get("A")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"3"}),
                ((CoursePrivateState)testResult.get("A")).getRegStudents()));

        assertEquals(3,(long)
                ((CoursePrivateState)testResult.get("B")).getAvailableSpots());
        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("B")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","3"}),
                ((CoursePrivateState)testResult.get("B")).getRegStudents()));

        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("C")).getAvailableSpots());
        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("C")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {}),
                ((CoursePrivateState)testResult.get("C")).getRegStudents()));

        assertEquals(0,(long)
                ((CoursePrivateState)testResult.get("D")).getAvailableSpots());
        assertEquals(2,(long)
                ((CoursePrivateState)testResult.get("D")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","3"}),
                ((CoursePrivateState)testResult.get("D")).getRegStudents()));
    }

    @Test(timeout=10000)
    public void testSimulationOne() {
        HashMap<String,PrivateState> testResult = executeTest("SimulationOne");

        assertEquals(7,(long)
                ((CoursePrivateState)testResult.get("A")).getAvailableSpots());
        assertEquals(3,(long)
                ((CoursePrivateState)testResult.get("A")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","2","3"}),
                ((CoursePrivateState)testResult.get("A")).getRegStudents()));

        assertEquals(7,(long)
                ((CoursePrivateState)testResult.get("A")).getAvailableSpots());
        assertEquals(3,(long)
                ((CoursePrivateState)testResult.get("A")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[] {"1","3","2"}),
                ((CoursePrivateState)testResult.get("A")).getRegStudents()));
    }

    @Test(timeout=10000)
    public void testSimulationTwo() {
        HashMap<String, PrivateState> testResult = executeTest("SimulationTwo");

        assertEquals(-1, (long)
                ((CoursePrivateState) testResult.get("A")).getAvailableSpots());
        assertEquals(0, (long)
                ((CoursePrivateState) testResult.get("A")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[]{}),
                ((CoursePrivateState) testResult.get("A")).getRegStudents()));

        assertEquals(-1, (long)
                ((CoursePrivateState) testResult.get("D")).getAvailableSpots());
        assertEquals(0, (long)
                ((CoursePrivateState) testResult.get("D")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[]{}),
                ((CoursePrivateState) testResult.get("D")).getRegStudents()));

        assertEquals(0, (long)
                ((CoursePrivateState) testResult.get("B")).getAvailableSpots());
        assertEquals(5, (long)
                ((CoursePrivateState) testResult.get("B")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[]{"1", "3", "4", "5", "6"}),
                ((CoursePrivateState) testResult.get("B")).getRegStudents()));

        assertEquals(A.createHash(new String[] {"B"},new Integer[] {-1}),
                ((StudentPrivateState)testResult.get("1")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("2")).getGrades());

        assertEquals(A.createHash(new String[] {"B"},new Integer[] {80}),
                ((StudentPrivateState)testResult.get("4")).getGrades());

        assertEquals(A.createHash(new String[] {"B"},new Integer[] {99}),
                ((StudentPrivateState)testResult.get("5")).getGrades());

        assertEquals(A.createHash(new String[] {"B"},new Integer[] {89}),
                ((StudentPrivateState)testResult.get("6")).getGrades());
    }

    @Test(timeout=10000)
    public void testSimulationThree() {
        HashMap<String, PrivateState> testResult = executeTest("SimulationThree");

        assertEquals(8, (long)
                ((CoursePrivateState) testResult.get("A")).getAvailableSpots());
        assertEquals(2, (long)
                ((CoursePrivateState) testResult.get("A")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[]{"3","5"}),
                ((CoursePrivateState) testResult.get("A")).getRegStudents()));

        assertEquals(1, (long)
                ((CoursePrivateState) testResult.get("B")).getAvailableSpots());
        assertEquals(4, (long)
                ((CoursePrivateState) testResult.get("B")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[]{"1","2","3","5"}),
                ((CoursePrivateState) testResult.get("B")).getRegStudents()));

        assertEquals(5, (long)
                ((CoursePrivateState) testResult.get("C")).getAvailableSpots());
        assertEquals(0, (long)
                ((CoursePrivateState) testResult.get("C")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[]{}),
                ((CoursePrivateState) testResult.get("C")).getRegStudents()));

        assertEquals(5, (long)
                ((CoursePrivateState) testResult.get("D")).getAvailableSpots());
        assertEquals(0, (long)
                ((CoursePrivateState) testResult.get("D")).getRegistered());
        assertTrue(A.containsSame(A.createVectorFromArray(new String[]{}),
                ((CoursePrivateState) testResult.get("D")).getRegStudents()));

        assertEquals(A.createHash(new String[] {"B"},new Integer[] {57}),
                ((StudentPrivateState)testResult.get("1")).getGrades());

        assertEquals(A.createHash(new String[] {"B"},new Integer[] {80}),
                ((StudentPrivateState)testResult.get("2")).getGrades());

        assertEquals(A.createHash(new String[] {"A","B"},new Integer[] {56,100}),
                ((StudentPrivateState)testResult.get("3")).getGrades());

        assertEquals(A.createHash(new String[] {},new Integer[] {}),
                ((StudentPrivateState)testResult.get("4")).getGrades());

        assertEquals(A.createHash(new String[] {"A","B"},new Integer[] {57,70}),
                ((StudentPrivateState)testResult.get("5")).getGrades());

        assertEquals(-1,
                ((StudentPrivateState)testResult.get("1")).getSignature());

        assertEquals(-1,
                ((StudentPrivateState)testResult.get("2")).getSignature());

        assertEquals(-2,
                ((StudentPrivateState)testResult.get("3")).getSignature());

        assertEquals(-2,
                ((StudentPrivateState)testResult.get("4")).getSignature());

        assertEquals(2,
                ((StudentPrivateState)testResult.get("5")).getSignature());

    }

    public static HashMap<String,PrivateState> executeTest(String testName){
        try {
            Simulator.main(new String[] {"src/test/resources/" + testName + ".json"});
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String,PrivateState> result = null;
        try {
            FileInputStream fis = new FileInputStream("result.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            result = (HashMap<String,PrivateState>)ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void executeCommand(String command){
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;

        try {
            while ((line = input.readLine()) != null)
                System.out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}