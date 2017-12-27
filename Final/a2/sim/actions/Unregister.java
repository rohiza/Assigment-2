package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.List;

public class Unregister extends Action {
    private String studentName;
    private String courseName;

    public Unregister(String studentName, String courseName){
        this.studentName = studentName;
        this.courseName = courseName;
        setActionName("Unregister");
    }

    protected void start() {
        CoursePrivateState cps = ((CoursePrivateState)actorState);
        if(!cps.getRegStudents().contains(studentName)){
            sendMessage(this, courseName, cps);
     //       System.out.println("I send the action agin " + this.getActionName());
        }
        else{
            List <Action<Boolean>> actions = new ArrayList<>();
            Action addSpaces = new addSpaces(1);
            Action removeStudentCourse = new removeCourse(courseName);
            actions.add(addSpaces);
            sendMessage(addSpaces, courseName, cps);
            actions.add(removeStudentCourse);
            sendMessage(removeStudentCourse, studentName, poolThreads.getPrivateState(studentName));
            then(actions,()->{
                if(actions.get(0).getResult().get()) {
                    cps.getRegStudents().remove(studentName);
                    cps.setRegistered(cps.getRegistered() - 1);
                    complete(true);
    //                System.out.println("I FINISH THE JOB "  + this.getActionName() + "\n");
                }
            });
        }
    }
}
