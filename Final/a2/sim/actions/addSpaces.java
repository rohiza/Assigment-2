package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.concurrent.atomic.AtomicInteger;

public class addSpaces extends Action {
    private Integer newSpace;

    public addSpaces(Integer newSpace){
        this.newSpace = newSpace;
        setActionName("Add Spaces");
    }

    protected void start() {
        CoursePrivateState cps = (CoursePrivateState) actorState;
        if(cps.getAvailableSpots() == -1 ) {
            complete(false);
   //         System.out.println("I failed " + this.getActionName());
        }
        else{
            cps.setAvailableSpots(cps.getAvailableSpots() + newSpace);
            complete(true);
  //          System.out.println("I FINISH THE JOB " + this.getActionName());
        }
    }
}
