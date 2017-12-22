package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

public class addStudent extends Action {
    private String name;

    public  addStudent(String studentName){
        this.name = studentName;
    }

    protected void start() {

    }
}
