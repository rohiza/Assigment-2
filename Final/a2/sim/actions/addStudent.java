package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import java.util.LinkedList;
import java.util.List;

public class addStudent extends Action {
    private String name;

    public  addStudent(String studentName){
        this.name = studentName;
        setActionName("Add Student");
    }

    protected void start() {
        StudentPrivateState newSPS = new StudentPrivateState();
        List<Action<Boolean>> actions = new LinkedList<>();
        Action open = new actionOpenActor();
        actions.add(open);
        sendMessage(open,name,newSPS);
        then(actions,()->{
                ((DepartmentPrivateState) actorState).getStudentList().add(name);
                complete(true);
    //        System.out.println("I FINISH THE JOB " + this.getActionName() + "\n");
        });
    }
}
