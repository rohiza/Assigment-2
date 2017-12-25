package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ParticipiatingInCourse extends Action {
    private String studentName;
    private String courseName;
    private Integer grade;

    public ParticipiatingInCourse(String studentName, String courseName, int grade){
        this.studentName = studentName;
        this.courseName = courseName;
        this.grade = grade;
        setActionName("Participate In Course");
    }

    protected void start() {
        CoursePrivateState cps = ((CoursePrivateState) actorState);
        if (cps.getAvailableSpots() == -1) {
            complete(false);
        }
        List<Action<Boolean>> actions = new ArrayList<>();
        if (cps.getRegStudents().contains(studentName)) {
            sendMessage(this, courseName, cps);
        }
        if (cps.getAvailableSpots() > 0) {
            Action checkPrequisites = new checkPrequisites(studentName, cps.getPrequisites());
            actions.add(checkPrequisites);
            sendMessage(checkPrequisites, studentName, poolThreads.getPrivateState(studentName));
            then(actions, () -> {
                if(actions.get(0).getResult().get()) {
                    if (cps.getAvailableSpots() == 1) {
                            toAdd(cps);
                            complete(true);
                    }
                    else if (cps.getAvailableSpots() > 1) {
                        toAdd(cps);
                        complete(true);
                    }
                }else{
                    complete(false);
                }
            });
        }else{
            complete(false);
        }
    }


    private void toAdd(CoursePrivateState cps){
        if(cps.getAvailableSpots() > 0) {
            Action addGrade = new addNewGrade(grade, courseName);
            sendMessage(addGrade, studentName, poolThreads.getPrivateState(studentName));
            cps.getRegStudents().add(studentName);
            cps.setAvailableSpots(cps.getAvailableSpots() - 1);
            cps.setRegistered(cps.getRegistered() + 1);
        }
    }
}
