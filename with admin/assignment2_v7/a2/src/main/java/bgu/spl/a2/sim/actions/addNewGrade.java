package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

public class addNewGrade  extends Action{
    private String course;
    private Integer grade;

    public addNewGrade(Integer grade, String course){
        this.course = course;
        this.grade = grade;
        setActionName("addNewGrade");
    }

    protected void start() {
        StudentPrivateState sps = ((StudentPrivateState)actorState);
        sps.getGrades().putIfAbsent(course, grade);
        complete(true);
    }
}
