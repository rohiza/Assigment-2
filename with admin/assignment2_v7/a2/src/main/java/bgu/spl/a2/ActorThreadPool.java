package bgu.spl.a2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {
	private int numThreads = 0;
	private ConcurrentHashMap<String, ConcurrentLinkedQueue> mapOfQuese;
	private ConcurrentHashMap<String, PrivateState> mapOfActor;
	private ConcurrentHashMap<String, AtomicBoolean> mapOfLocks;
	private Workers[] threads;
	protected VersionMonitor vm = new VersionMonitor();

	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 * <p>
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads the number of threads that should be started by this thread
	 *                 pool
	 */
	public ActorThreadPool(int nthreads) {
		numThreads = nthreads;
		mapOfQuese = new ConcurrentHashMap<>();
		mapOfActor = new ConcurrentHashMap<>();
		mapOfLocks = new ConcurrentHashMap<>();
		threads = new Workers[numThreads];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Workers();
		}
	}

	/**
	 * getter for actors
	 *
	 * @return actors
	 */
	public Map<String, PrivateState> getActors() {
		return mapOfActor;
	}

	/**
	 * getter for actor's private state
	 *
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivateState(String actorId) {
		return mapOfActor.get(actorId);
	}


	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action     the action to execute
	 * @param actorId    corresponding actor's id
	 * @param actorState actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState) {
		if (mapOfActor.putIfAbsent(actorId, actorState) == null) {
			mapOfQuese.putIfAbsent(actorId, new ConcurrentLinkedQueue());
			mapOfLocks.putIfAbsent(actorId, new AtomicBoolean());
		}
		mapOfQuese.get(actorId).add(action);
		vm.inc();
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 * <p>
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {
		for (Thread t : threads) {
			t.interrupt();
		}
		for (int i = 0; i < threads.length; i++) {
			while (threads[i].isAlive()) ;
		}

	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}

	}

	private class Workers extends Thread {
		public void run() {
			while (!currentThread().isInterrupted()) {
				Integer momentVersion = vm.getVersion();
				for (Map.Entry entry : mapOfLocks.entrySet()) {
					String actorid = (String) entry.getKey();
					AtomicBoolean f = (AtomicBoolean) entry.getValue();
					if(actorid !=null ){
					if (f.compareAndSet(false, true)) {
						if (!mapOfQuese.get(actorid).isEmpty()) {
							Action<?> task = (Action) mapOfQuese.get(actorid).poll();
							task.handle(ActorThreadPool.this, actorid, getPrivateState(actorid));
							f.compareAndSet(true, false);
							vm.inc();
						} else {
							f.compareAndSet(true, false);

						}

				/*
				mapOfLocks.forEach((k, v) -> {
					if (v.compareAndSet(false, true)) {
						if (!mapOfQuese.get(k).isEmpty()) {
							Action<?> task = (Action) mapOfQuese.get(k).poll();
							task.handle(ActorThreadPool.this, k, getPrivateState(k));
							v.compareAndSet(true, false);
							vm.inc();
						} else {
							v.compareAndSet(true, false);
						}
					}
				});
*/
						mapOfLocks.forEach((k, v) -> {
							if (!v.get())
								vm.inc();
						});

						try {
							vm.await(momentVersion);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}
			}
		}
	}

	}
}



