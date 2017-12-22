package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.LinkedList;

public class openNewCourse extends Action {
    private String deprtmantName;
    private String  courseName;
    private Integer numOfspace;
    private LinkedList<String> prerequisites = new LinkedList<>();
    public openNewCourse(String deprtmantName,String courseName,Integer spaces , LinkedList pre){
        this.deprtmantName = deprtmantName;
        this.courseName = courseName;
        this.numOfspace = spaces;
        for (Object e : pre){
            prerequisites.add((String) e);
        }

    }
    @Override
    protected void start() {
       sendMessage(new actionOpenActor(),getDeprtmantName(),new DepartmentPrivateState());
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
