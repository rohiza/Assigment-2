package bgu.spl.a2.sim;

import org.junit.Test;
import org.omg.CORBA.COMM_FAILURE;

import java.util.Vector;

import static org.junit.Assert.*;

public class WarehouseTest {
    @Test(timeout=10000)
    public void testMethods(){
        Vector<Computer> computers = new Vector<>();

        for(int i = 0;i < 10;i++){
            Computer comp = new Computer("" + i);
            comp.successSig = i;
            comp.failSig = -i;

            computers.add(comp);
        }
/*
        Warehouse warehouse = new Warehouse(computers);

        for(int i = 0;i < 10;i++){
            assertEquals(warehouse.getComputer("" + i).down().get(),computers.get(i));
            warehouse.mutexToComputer("" + i).up();
        }
        */

    }
}