package bgu.spl.a2.sim;
import bgu.spl.a2.Promise;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * 
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	private ConcurrentLinkedQueue<Promise<Computer>> wList;
	private AtomicBoolean isWaiting;
	private Computer comp;
	
	/**
	 * Constructor
	 * @param computer
	 */
	public SuspendingMutex(Computer computer){
		this.wList = new ConcurrentLinkedQueue<Promise<Computer>>();
		this.isWaiting = new AtomicBoolean();
		this.comp = computer;
	}
	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 * 
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down(){
		Promise<Computer> toWait = new Promise<>();
		if(isWaiting.compareAndSet(false, true)){
			toWait.resolve(comp);
		} else{
			wList.add(toWait);
		}
		return toWait;
	}
	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up(){
		if(wList.isEmpty()){
			isWaiting.compareAndSet(true, false);
		} else{
			wList.poll().resolve(comp);
		}
	}

}
