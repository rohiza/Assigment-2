package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;

public class closeAcourse extends Action{
    private String courseName;

    public closeAcourse(String courseName){
        this.courseName = courseName;
    }

    protected void start() {
        DepartmentPrivateState dps = (DepartmentPrivateState) actorState;
        CoursePrivateState cps = (CoursePrivateState) poolThreads.getPrivateState(courseName);
        List<Action<Boolean>> actions = new ArrayList<>();
        for (Object e : cps.getRegStudents()) {
            Action x = new Unregister((String) e, courseName);
            actions.add(x);
            sendMessage(x, courseName,cps);
        }
        then(actions,()->{dps.getCourseList().remove(courseName);
            cps.setAvailableSpots(-1);
            complete(true);});

 //       System.out.println("I FINISH THE JOB " + this.getActionName());
    }
}
