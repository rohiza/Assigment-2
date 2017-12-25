package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class removeCourse extends Action {
    private String courseName;

    public removeCourse(String courseName){
        this.courseName = courseName;
        setActionName("removeCourse");
    }

    protected void start() {
        StudentPrivateState sps = ((StudentPrivateState)actorState);
        sps.getGrades().remove(courseName);
        complete(true);
    }
}
