package sharedRegions;

import commInfra.*;
import entities.*;
import genclass.*;
import main.SimulConsts;

public class ControlCollectionSite {

    /**
     * Indicate ordinary thieve handed a canvas
     */
    private boolean handed;

    /**
     * Indicate master thieve as collected the canvas
     */
    private boolean collected;



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

    



    public synchronized void startOperation(){
        //Update Master state
		((Master) Thread.currentThread()).setMasterState(MasterStates.DECIDING_WHAT_TO_DO);
		repos.setMasterState(((Master) Thread.currentThread()).getMasterState());
    }


    public synchronized int takeARest(){

        while(!handed){
			try { wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

        //Update Master state
		((Master) Thread.currentThread()).setMasterState(MasterStates.WAITING_FOR_GROUP_ARRIVAL);
		repos.setMasterState(((Master) Thread.currentThread()).getMasterState());

        return 0;
    }

    public synchronized void handACanvas(){
        notifyAll();
        handed = true;

        while(!collected){
			try { wait();
			} catch (InterruptedException e) {
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
