package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

public class actionOpenActor  extends Action{

    public  actionOpenActor(){ setActionName("Action Open Actor");}

    protected void start() {
        complete(true);
    }
}
