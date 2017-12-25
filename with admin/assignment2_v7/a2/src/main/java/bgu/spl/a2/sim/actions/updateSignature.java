package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.LinkedList;

public class updateSignature extends Action {
    private LinkedList<String> obligations;
    private Computer thisComp;

    public updateSignature(Computer thisComp, LinkedList<String> obligations){
        this.thisComp = thisComp;
        this.obligations = obligations;
        setActionName("updateSignature");
    }

    protected void start() {
        StudentPrivateState sps = ((StudentPrivateState)actorState);
        sps.setSignature(thisComp.checkAndSign(obligations, sps.getGrades()));
        complete(true);
    }
}
