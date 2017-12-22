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
    }

    protected void start() {
        StudentPrivateState newSPS = new StudentPrivateState();
        List<Action<Boolean>> actions = new LinkedList<>();
        Action open = new actionOpenActor();
        actions.add(open);
        sendMessage(open,name,newSPS);
        then(actions,()->{
            if(actions.get(0).getResult().get()) {
                ((DepartmentPrivateState) actorState).getStudentList().add(name);
                complete(true);
            }
        });
    }
}
