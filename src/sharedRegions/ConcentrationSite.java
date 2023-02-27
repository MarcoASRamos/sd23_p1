package sharedRegions;

import commInfra.*;
import entities.*;
import genclass.*;
import main.SimulConsts;

public class ConcentrationSite {



    /**
     *   Reference to the general repository.
     */
    private final GeneralRepos repos;

    /**
     * Bar instantiation
     *
     * @param repos reference to the general repository
     */
    public ConcentrationSite(GeneralRepos repos){
        
        this.repos = repos;
    }
    

    public synchronized char appraiseSit(){
        return 'a';
    }

    public synchronized void prepareAssaultParty(){
        
    }

    public synchronized boolean amINeeded(){
        return true;
    }

    public synchronized int prepareExcursion(){
        return 0;
    }

    public synchronized void sumUpResults(){
        
    }
}
