package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;

public class checkPrequisites extends Action {
    private String studentName;
    private List<String> prequisites;

    public checkPrequisites(String studentName, List<String> prequisites){
        this.studentName = studentName;
        this.prequisites = prequisites;
    }

    protected void start() {
        StudentPrivateState sps = ((StudentPrivateState)actorState);
        for(Object e: prequisites){
            String courseName = (String) e;
            if(sps.getGrades().get(courseName) == null || sps.getGrades().get(courseName) < 56){
                complete(false);
                return;
            }
        }
        complete(true);
    }

}
