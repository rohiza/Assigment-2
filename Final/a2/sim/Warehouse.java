package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse {
    private ArrayList<Computer> computerList;
    private ConcurrentHashMap<String, SuspendingMutex> mutexMap;

    public Warehouse(ArrayList<Computer> compArray){
        this.computerList = compArray;
        this.mutexMap = new ConcurrentHashMap<>();
        computerList.forEach(s->{
            mutexMap.putIfAbsent(s.getComputerType(),s.getCompMutex());
        });
    }

    public Promise<Computer> isFree (String compType){
        return mutexMap.get(compType).down();
    }

    public void releaseComputer(String compType){
        mutexMap.get(compType).up();
    }

    public Computer getComputer(String compType){
        for(Computer s: computerList){
            if(s.getComputerType().equals(compType)){
                return s;
            }
        }
        return null;
    }

}
