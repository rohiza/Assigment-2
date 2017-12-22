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

    public openNewCourse(String deprtmantName,String courseName,Integer spaces , LinkedList pre){
        this.deprtmantName = deprtmantName;
        this.courseName = courseName;
        this.numOfspace = spaces;
        this.prerequisites = pre;
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
           if(actions.get(0).getResult().get()) {
               ((DepartmentPrivateState) actorState).getCourseList().add(courseName);
               complete(true);
           }
       });
    }

    public String getDeprtmantName() {
        return deprtmantName;
    }

    public void setDeprtmantName(String deprtmantName) {
        this.deprtmantName = deprtmantName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getNumOfspace() {
        return numOfspace;
    }

    public void setNumOfspace(Integer numOfspace) {
        this.numOfspace = numOfspace;
    }

    public LinkedList<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(LinkedList<String> prerequisites) {
        this.prerequisites = prerequisites;
    }
}
