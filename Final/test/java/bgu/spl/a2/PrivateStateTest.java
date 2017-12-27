package bgu.spl.a2;

import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;

public class PrivateStateTest {

    @Test(timeout=10000)
    public void testLogger() {
        // Creating an anonymous private state for the test
        PrivateState priState = new PrivateState(){};
        priState.addRecord("Hello");
        priState.addRecord("What");

        // Creating a vector to assert history of private state equals
        Vector<String> history = new Vector<>();
        history.add("Hello");
        history.add("What");

        // Asserting history of private state is as expected
        assertEquals(history,priState.getLogger());
    }

}