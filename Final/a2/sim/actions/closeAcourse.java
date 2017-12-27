package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

public class closeAcourse extends Action{
    private String courseName;

    public closeAcourse(String courseName){
        this.courseName = courseName;
    }

    protected void start() {
        DepartmentPrivateState dps = (DepartmentPrivateState) actorState;
        CoursePrivateState cps = (CoursePrivateState) poolThreads.getPrivateState(courseName);
        for (Object e : cps.getRegStudents()) {
            sendMessage(new Unregister((String) e, courseName), courseName, poolThreads.getPrivateState(courseName));
        }
        dps.getCourseList().remove(courseName);
        cps.setAvailableSpots(-1);
        complete(true);
 //       System.out.println("I FINISH THE JOB " + this.getActionName());
    }
}
