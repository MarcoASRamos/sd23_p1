package sharedRegions;

import commInfra.*;
import entities.*;
import genclass.*;
import main.SimulConsts;

public class Museum {

    /**
     * Reference to the general repository.
     */
    private final GeneralRepos repos;

    /**
     * Bar instantiation
     *
     * @param repos reference to the general repository
     */
    public Museum(GeneralRepos repos) {

        this.repos = repos;
    }

    public synchronized void rollACanvas() {
        //Update Ordinary state
        int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
        ((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.AT_A_ROOM);
        repos.setOrdinaryState(ordinaryId, ((Ordinary) Thread.currentThread()).getOrdinaryState());
    }

}
