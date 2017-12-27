package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import java.util.LinkedList;
import java.util.List;

public class openNewCourse extends Action {
    private String deprtmantName;
    private String  courseName;
    private Integer numOfspace;
    private LinkedList<String> prerequisites ;

    public openNewCourse(String departmantName,String courseName,Integer spaces , LinkedList pre){
        this.deprtmantName = departmantName;
        this.courseName = courseName;
        this.numOfspace = spaces;
        this.prerequisites = pre;
        setActionName("Open Course");
    }

    protected void start() {
        CoursePrivateState newCPS = new CoursePrivateState();
        newCPS.setPrequisites(this.prerequisites);
        newCPS.setAvailableSpots(this.numOfspace);
        List<Action<Boolean>> actions = new LinkedList<>();
        Action open = new actionOpenActor();
        actions.add(open);
       sendMessage(open,courseName,newCPS);
       then(actions,()->{
      //     System.out.println("Callback open Course has has been called");
               ((DepartmentPrivateState) actorState).getCourseList().add(courseName);
               complete(true);
      //     System.out.println("I FINISH THE JOB " + this.getActionName() + "\n");
       });
    }


}
