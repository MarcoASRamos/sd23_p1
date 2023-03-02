package sharedRegions;

import commInfra.*;
import entities.*;
import genclass.*;
import main.SimulConsts;

public class ConcentrationSite {

    /**
     * Reference to the general repository.
     */
    private final GeneralRepos repos;

    /**
     * Bar instantiation
     *
     * @param repos reference to the general repository
     */
    public ConcentrationSite(GeneralRepos repos) {

        this.repos = repos;
    }

    public synchronized int appraiseSit() {
        return 0;
    }

    public synchronized int prepareAssaultParty() {
        notifyAll();
        // preparing = true;

        while (excursion < 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        int exc = excursion;
        excursion = -1;

        // Update Master state
        ((Master) Thread.currentThread()).setMasterState(MasterStates.ASSEMBLING_A_GROUP);
        repos.setMasterState(((Master) Thread.currentThread()).getMasterState());

        return exc;
    }

    public synchronized boolean amINeeded(){

        while(preparing or results){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        boolean needed = preparing;
        preparing = false;


        //Update Ordinary state
		((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.CONCENTRATION_SITE);
		repos.setOrdinaryState(((Ordinary) Thread.currentThread()).getOrdinaryState());

        return needed;
    }

    public synchronized int prepareExcursion() {

        notifyAll();
        excursion = 0;

        // Update Ordinary state
        ((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.CRAWLING_INWARDS);
        repos.setOrdinaryState(((Ordinary) Thread.currentThread()).getOrdinaryState());

        return 0;
    }

    public synchronized void sumUpResults() {
        notifyAll();
        results = true;

        // Update Master state
        ((Master) Thread.currentThread()).setMasterState(MasterStates.PRESENTING_THE_REPORT);
        repos.setMasterState(((Master) Thread.currentThread()).getMasterState());

    }
}
