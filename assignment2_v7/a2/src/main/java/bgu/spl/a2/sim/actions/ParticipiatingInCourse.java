package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ParticipiatingInCourse extends Action {
    private String studentName;
    private String courseName;
    private Integer grade;
    private AtomicInteger checkSpaces;

    public ParticipiatingInCourse(String studentName, String courseName,int grade){
        this.studentName = studentName;
        this.courseName = courseName;
        this.grade = grade;
        this.checkSpaces = new AtomicInteger(((CoursePrivateState) actorState).getAvailableSpots());
    }

    protected void start() {
        CoursePrivateState cps = ((CoursePrivateState) actorState);
        List<Action<Boolean>> actions = new LinkedList<>();
        if (cps.getRegStudents().contains(studentName)){
            sendMessage(this, courseName, cps);
            complete(true);
        }
        else {
            if (checkSpaces.get() > 0) {
                Action checkPrequisites = new checkPrequisites(studentName, cps.getPrequisites());
                actions.add(checkPrequisites);
                sendMessage(checkPrequisites, studentName, poolThreads.getPrivaetState(studentName));
                then(actions, () -> {
                    if (actions.get(0).getResult().get()) {
                        if (checkSpaces.get() == 1) {
                            if (checkSpaces.compareAndSet(1, 0)) {
                                toAdd(cps);
                                complete(true);
                            } else {
                                return;
                            }
                        } else if (checkSpaces.get() > 1) {
                            toAdd(cps);
                            complete(true);
                        }
                    }
                });
            }
        }
    }

    private void toAdd(CoursePrivateState cps){
        Action addGrade = new addNewGrade(grade , courseName);
        sendMessage(addGrade, studentName, poolThreads.getPrivaetState(studentName));
        cps.getRegStudents().add(studentName);
        cps.setAvailableSpots(cps.getAvailableSpots() - 1);
    }
}
