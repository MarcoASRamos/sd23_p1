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

        while(!handed){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        //Update Master state
		((Master) Thread.currentThread()).setMasterState(MasterStates.WAITING_FOR_GROUP_ARRIVAL);
		repos.setMasterState(((Master) Thread.currentThread()).getMasterState());

    }

    public synchronized void handACanvas(){

        notifyAll();
        handed = true;

        while(!collected){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        collected = false;


    }

    public synchronized void collectACanvas(){
        notifyAll();
        collected = true;

        //Update Master state
		((Master) Thread.currentThread()).setMasterState(MasterStates.DECIDING_WHAT_TO_DO);
		repos.setMasterState(((Master) Thread.currentThread()).getMasterState());

    }
    
}
