package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.concurrent.atomic.AtomicInteger;

public class addSpaces extends Action {
    private Integer newSpace;
    private AtomicInteger space;

    public addSpaces(Integer newSpace){
        this.newSpace = newSpace;
        space = new AtomicInteger(((CoursePrivateState)actorState).getAvailableSpots());
    }

    protected void start() {
        CoursePrivateState cps = ((CoursePrivateState)actorState);
        if(space.compareAndSet(space.get(), space.get() + newSpace)) {
            cps.setAvailableSpots(space.get());
            complete(true);
        }
    }
}
