package bgu.spl.a2.sim;

import org.junit.Test;

import java.util.HashMap;
import java.util.Vector;

import static org.junit.Assert.*;

public class ComputerTest {
    @Test(timeout=10000)
    public void testMethods(){
        Computer comp = new Computer("A");
        comp.successSig = 1;
        comp.failSig = 0;

        Vector<String> cond1 = createConditions(0);
        Vector<String> cond2 = createConditions(5);
        Vector<String> cond3 = createConditions(10);

        // Pass only 1
        HashMap<String, Integer> stdu1 = createStudentGrades(new Integer[] {58,57,100});

        // Pass 1 and 2
        HashMap<String, Integer> stdu2 = createStudentGrades(new Integer[] {58,57,100,58,60});

        // Pass only 1
        HashMap<String, Integer> stdu3 = createStudentGrades(new Integer[] {56,57,100,58,60});

        // Pass only 1,2 and 3
        HashMap<String, Integer> stdu4 = createStudentGrades(new Integer[] {57,57,70,58,60,90,75,80,99,120});

        // Pass only 1 and 2
        HashMap<String, Integer> stdu5 = createStudentGrades(new Integer[] {57,57,70,58,60,90,75,20,99,120});

        // Pass only 1
        HashMap<String, Integer> stdu6 = createStudentGrades(new Integer[] {57,57,56,58,60,90,75,80,99,120});

        // Pass only 1
        HashMap<String, Integer> stdu7 = createStudentGrades(new Integer[] {10,57,59,58,60,90,75,80,99,120});

        // Pass only 1 and 2
        HashMap<String, Integer> stdu8 = createStudentGrades(new Integer[] {58,57,59,58,60,90,75,80,99,56});

        assertEquals(1,comp.checkAndSign(cond1,stdu1));
        assertEquals(0,comp.checkAndSign(cond2,stdu1));
        assertEquals(0,comp.checkAndSign(cond3,stdu1));

        assertEquals(1,comp.checkAndSign(cond1,stdu2));
        assertEquals(1,comp.checkAndSign(cond2,stdu2));
        assertEquals(0,comp.checkAndSign(cond3,stdu2));

        assertEquals(1,comp.checkAndSign(cond1,stdu3));
        assertEquals(0,comp.checkAndSign(cond2,stdu3));
        assertEquals(0,comp.checkAndSign(cond3,stdu3));

        assertEquals(1,comp.checkAndSign(cond1,stdu4));
        assertEquals(1,comp.checkAndSign(cond2,stdu4));
        assertEquals(1,comp.checkAndSign(cond3,stdu4));

        assertEquals(1,comp.checkAndSign(cond1,stdu5));
        assertEquals(1,comp.checkAndSign(cond2,stdu5));
        assertEquals(0,comp.checkAndSign(cond3,stdu5));

        assertEquals(1,comp.checkAndSign(cond1,stdu6));
        assertEquals(0,comp.checkAndSign(cond2,stdu6));
        assertEquals(0,comp.checkAndSign(cond3,stdu6));

        assertEquals(1,comp.checkAndSign(cond1,stdu7));
        assertEquals(0,comp.checkAndSign(cond2,stdu7));
        assertEquals(0,comp.checkAndSign(cond3,stdu7));

        assertEquals(1,comp.checkAndSign(cond1,stdu8));
        assertEquals(1,comp.checkAndSign(cond2,stdu8));
        assertEquals(0,comp.checkAndSign(cond3,stdu8));
    }

    public Vector<String> createConditions(Integer numOfConds){
        Vector<String> conds = new Vector<>();

        for(int i = 0;i < numOfConds;i++){
            conds.add("" + i);
        }

        return conds;
    }

    public HashMap<String, Integer> createStudentGrades(Integer[] grades){
        HashMap<String, Integer> student = new HashMap<>();
        Vector<String> conds = createConditions(grades.length);

        for(int i = 0;i < grades.length;i++){
            student.put(conds.get(i),grades[i]);
        }

        return student;
    }

}