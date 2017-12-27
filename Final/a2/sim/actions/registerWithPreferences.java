package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class registerWithPreferences extends Action{
    private String id;
    private LinkedList<String> preferences;
    private LinkedList<Integer> grades;

    public registerWithPreferences(String id, LinkedList<String> preferences, LinkedList<Integer> grades){
        this.id = id;
        this.preferences = preferences;
        this.grades = grades;
        setActionName("Register With Preferences");
    }

    protected void start() {
        if(preferences.size() ==0) {
            complete(false);
        }
        else {
            StudentPrivateState sps = ((StudentPrivateState) actorState);
            Action x = new ParticipiatingInCourse(id, preferences.getFirst(), grades.getFirst());
            List<Action<Boolean>> actions = new LinkedList<>();
            sendMessage(x, preferences.getFirst(), poolThreads.getPrivateState(preferences.getFirst()));
            actions.add(x);
            then(actions, () -> {
                if (actions.get(0).getResult().get()) {
                    complete(true);
                } else {
                    preferences.removeFirst();
                    grades.removeFirst();
                    start();
                }
            });
        }
    }
}
