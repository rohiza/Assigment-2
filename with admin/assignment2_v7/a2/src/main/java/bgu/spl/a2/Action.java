package bgu.spl.a2;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {
    private String actionName;
    protected String actorId;
    private Promise<R> promise;
    protected callback callback ;
    protected ActorThreadPool poolThreads;
    protected PrivateState actorState;
    /**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     */
    protected abstract void start();


    /**
     *
     * start/continue handling the action
     *
     * this method should be called in order to start this action
     * or continue its execution in the case where it has been already started.
     *
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     */
    /*package*/ final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
        this.poolThreads = pool;
        this.actorId = actorId;
        this.actorState = actorState;
        if(callback == null){
            start();
            actorState.addRecord(getActionName());
        }
        else{
            callback.call();
        }

    }


    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     *
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void then(Collection<? extends Action<?>> actions, callback callback) {
        AtomicInteger numOfactions = new AtomicInteger(0);
        this.callback = callback;
        if(actions.size() == 0)
            sendMessage(this,actorId,actorState);
        for (Object a: actions) {
            ((Action) a).getResult().subscribe(()->{
            if (numOfactions.get() < actions.size()) {
                numOfactions.incrementAndGet();
            }
            if(numOfactions.get() == actions.size()){
                sendMessage(this, actorId, actorState);
            }
            });
        }
    }




    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
        this.getResult().resolve(result);
    }

    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
        if(promise == null)
            promise = new Promise<>();
        return promise;
    }

    /**
     * send an action to an other actor
     *
     * @param action
     * 				the action
     * @param actorId
     * 				actor's id
     * @param actorState
     * 				actor's private state (actor's information)
     *
     * @return promise that will hold the result of the sent action
     */
    public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState){
        poolThreads.submit(action,actorId,actorState);
        return action.promise;
    }

    /**
     * set action's name
     * @param actionName
     */
    public void setActionName(String actionName){
        this.actionName = actionName;
    }

    /**
     * @return action's name
     */
    public String getActionName(){
        return  actionName;
    }
}
