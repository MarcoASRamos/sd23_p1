package sharedRegions;

import commInfra.*;
import entities.*;
import genclass.*;
import main.SimulConsts;

public class ConcentrationSite {


    /**
     * Ordinary thieves Queue.
     */
    private MemFIFO<Integer> thievesQueue;

    /**
     * Number of ordinary thieves waiting master decision.
     */
    private int waitingThieves;

    /**
     * Excursion party selected
     */
    private int excursion;

    /**
     * Results 
     */
    private boolean results;

    /**
     * Construct assault party 
     */
    private int constructAP;

    /**
     * Preparing thieve to an assault party
     */
    private int preparing;

    /**
     * Assault party to join
     */
    private int ap;



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
        try { this.thievesQueue = new MemFIFO<>(new Integer[SimulConsts.M-1]);
        } catch (MemException e) {
            GenericIO.writelnString ("Instantiation of waiting FIFO failed: " + e.getMessage ());
            System.exit (1);
        }

        this.waitingThieves = 0;
        this.excursion = -1;
        this.results = false;
        this.constructAP = 0;
        this.preparing = 0;
        this.ap = -1;
    }






    /**
     * The master thief appraise the situation of how the heist is going 
     * and takes a decision of is next step based on that
     * 
     * @return
     */
    public synchronized int appraiseSit() {
        //ja foi tudo roubado
        if(false) return 3;

        //se a fila estiver vazia e algum ap tiver em acçao
        //if(waitingThieves==0 && !avb_ap0 || !avb_ap1)
        if(waitingThieves==0)
            return 2;

        while (waitingThieves==0) {
            try {  wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        
        constructAP++;
        if(constructAP>=SimulConsts.E) return 1;

        return 0;
    }





    /**
     * The master thief prepares an assault party to lauch in excursion
     */
    public synchronized void prepareAssaultParty(int ap) {
        this.ap = ap;
        while (excursion < SimulConsts.E) {
            try {
                preparing = thievesQueue.read();
                waitingThieves--;
            } catch (Exception e) {}
            notifyAll();

            try { wait();
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }
        }
        this.ap = -1;
        excursion -= SimulConsts.E;

        // Update Master state
        ((Master) Thread.currentThread()).setMasterState(MasterStates.ASSEMBLING_A_GROUP);
        repos.setMasterState(((Master) Thread.currentThread()).getMasterState());
    }


    /**
     * The ordinary thieve makes the last preparations before going in an excursion
     * 
     * @return joined assault party
     */
    public synchronized int prepareExcursion() {
        excursion++;
        notifyAll();

        // Update Ordinary state
        int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
        ((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.CRAWLING_INWARDS);
        repos.setOrdinaryState(ordinaryId, ((Ordinary) Thread.currentThread()).getOrdinaryState());

        return excursion*10+ap;
    }




    /**
     * The ordinary thief indicates to the master that he is available
     * 
     * @return master service decision
     */
    public synchronized boolean amINeeded(){

        int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
        try {
            thievesQueue.write(ordinaryId);
            waitingThieves++;
        } catch (Exception e) {}
        notifyAll();


        while(preparing!=ordinaryId || !results){
			try { wait();
			} catch (InterruptedException e) {
				GenericIO.writelnString(" "+e.getMessage());
                System.exit(0);
			}
		}


        //Update Ordinary state
		((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.CONCENTRATION_SITE);
		repos.setOrdinaryState(ordinaryId, ((Ordinary) Thread.currentThread()).getOrdinaryState());

        return preparing==ordinaryId;
    }



    /**
     * All the paitings were finally robbed, now its time to count the gains
     */
    public synchronized void sumUpResults() {
        notifyAll();
        results = true;

        // Update Master state
        ((Master) Thread.currentThread()).setMasterState(MasterStates.PRESENTING_THE_REPORT);
        repos.setMasterState(((Master) Thread.currentThread()).getMasterState());

    }
}
