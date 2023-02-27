package sharedRegions;

import commInfra.*;
import entities.*;
import genclass.*;
import main.SimulConsts;

public class ControlCollectionSite {




    /**
     *   Reference to the general repository.
     */
    private final GeneralRepos repos;

    /**
     * Bar instantiation
     *
     * @param repos reference to the general repository
     */
    public ControlCollectionSite(GeneralRepos repos){
        
        this.repos = repos;
    }

    public synchronized void takeARest(){
        
    }

    public synchronized void handACanvas(){
        
    }

    public synchronized void collectACanvas(){
        
    }
    
}
