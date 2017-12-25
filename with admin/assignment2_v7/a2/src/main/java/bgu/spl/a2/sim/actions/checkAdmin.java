package bgu.spl.a2.sim.actions;

        import bgu.spl.a2.Action;
        import bgu.spl.a2.sim.Computer;
        import bgu.spl.a2.sim.Simulator;
        import bgu.spl.a2.sim.Warehouse;
        import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

        import java.util.LinkedList;
        import java.util.List;

public class checkAdmin extends Action {
    private LinkedList<String> studentsId;
    private LinkedList<String> obligations;
    private String computerType;

    public checkAdmin(String computerType, LinkedList<String> studentsId, LinkedList<String> obligations){
        this.computerType = computerType;
        this.studentsId = studentsId;
        this.obligations = obligations;
        setActionName("Administrative Check");
    }

    protected void start() {
        DepartmentPrivateState dps = ((DepartmentPrivateState)actorState);
        List<Action<Boolean>> actions = new LinkedList<>();
        if(Simulator.currWarehouse.isFree(computerType).isResolved()){
            for(String a: studentsId) {
                Action x = new updateSignature(Simulator.currWarehouse.getComputer(computerType), obligations);
                sendMessage(x, a, poolThreads.getPrivateState(a));
            }
            complete(true);
        }
        else{
            Simulator.currWarehouse.releaseComputer(computerType);
            Simulator.currWarehouse.isFree(computerType).subscribe(()->{
                sendMessage(this, actorId, dps);
            });


    }
}
}