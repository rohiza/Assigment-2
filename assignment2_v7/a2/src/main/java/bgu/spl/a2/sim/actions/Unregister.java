package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.List;

public class Unregister extends Action {
    private String studentName;
    private String courseName;

    public Unregister(String studentName, String courseName){
        this.studentName = studentName;
        this.courseName = courseName;
    }

    protected void start() {
        CoursePrivateState cps = ((CoursePrivateState)actorState);
        if(!cps.getRegStudents().contains(studentName)){
            sendMessage(this, courseName, cps);
            complete(true);
        }
        else{
            cps.getRegStudents().remove(studentName);
            sendMessage(new removeCourse(courseName), studentName, poolThreads.getPrivaetState(studentName));
            sendMessage(new addSpaces(1), courseName, cps);
            complete(true);
        }
    }
}
