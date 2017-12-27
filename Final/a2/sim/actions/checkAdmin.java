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
    private Warehouse warehouse;

    public checkAdmin(String computerType, LinkedList<String> studentsId, LinkedList<String> obligations,Warehouse warehouse){
        this.computerType = computerType;
        this.studentsId = studentsId;
        this.obligations = obligations;
        this.warehouse =warehouse;
        setActionName("Administrative Check");
    }

    protected void start() {
 //       System.out.println(Thread.currentThread()+ "I am the ADMIN CHECKER");
        DepartmentPrivateState dps = ((DepartmentPrivateState)actorState);
        List<Action<Boolean>> actions = new LinkedList<>();
        if(warehouse.isFree(computerType).isResolved()){
            for(String a: studentsId) {
                Action x = new updateSignature(warehouse.getComputer(computerType), obligations);
                sendMessage(x, a, poolThreads.getPrivateState(a));
            }
            complete(true);
   //         System.out.println("I FINISH THE JOB " + this.getActionName() +"\n");
        }
        else{
    //        System.out.println(" got here ");
          //  Simulator.currWarehouse.releaseComputer(computerType);
            warehouse.isFree(computerType).subscribe(()->{
                System.out.println("Admin Send up ");
                sendMessage(this, actorId, dps);
            });


    }
}
}
