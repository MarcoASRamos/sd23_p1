package sharedRegions;

import commInfra.*;
import entities.*;
import genclass.*;
import main.SimulConsts;

public class AssaultParty0 {



    /**
     *   Reference to the general repository.
     */
    private final GeneralRepos repos;

    /**
     * Bar instantiation
     *
     * @param repos reference to the general repository
     */
    public AssaultParty0(GeneralRepos repos){
        
        this.repos = repos;
    }

    public synchronized void ReverseDirection(){
        //Update Ordinary state
		((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.CRAWLING_OUTWARDS);
		repos.setOrdinaryState(((Ordinary) Thread.currentThread()).getOrdinaryState());

    }

    public synchronized int sendAssaultParty(){
        //Update Master state
		((Master) Thread.currentThread()).setMasterState(MasterStates.DECIDING_WHAT_TO_DO);
		repos.setMasterState(((Master) Thread.currentThread()).getMasterState());

        return 0;
    }


    public synchronized boolean crawlIn(int member){

		while(false){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        if(){
            //Update Ordinary state
            ((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.AT_A_ROOM);
            repos.setOrdinaryState(((Ordinary) Thread.currentThread()).getOrdinaryState());
        }

        return true;
    }

    public synchronized boolean crawlOut(int member){

        while(false){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        if(){
            //Update Ordinary state
		    ((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.COLLECTION_SITE);
		    repos.setOrdinaryState(((Ordinary) Thread.currentThread()).getOrdinaryState());
        }

        return true;
    }
    
}