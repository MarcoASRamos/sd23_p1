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
     * Master is going to sum up the results 
     */
    private boolean results;

    /**
     * Summon thieve to an assault party
     */
    private int summon;

    /**
     * Preparingassault Party
     */
    private int preparingAP;

    /**
     * Number of members on Assault party 0
     */
    private int ap0;

    /**
     * Indicate which the Assault party is heisting
     */
    private int rooms[];

    /**
     * Number of members on Assault party 1
     */
    private int ap1;


    /**
     * Return one assault party available
     * 
     * @return assault party
     */
    public int getAssautlParty(){
        return rooms[0]<0? 0:1;
    }

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
        this.summon = 0;
        this.preparingAP = -1;
        this.ap0 = 0;
        this.ap1 = 0;
        this.rooms = new int[2];
        for(int i=0; i<2; i++) rooms[i]=-1; 
    }






    /**
     * The master thief appraise the situation of how the heist is going 
     * and takes a decision of is next step based on that
     * 
     * @param roomState indicate if all rooms are empty
     * @return the master decision
     */
    public synchronized int appraiseSit(boolean roomState) {
        if(roomState) return 3;
        if(waitingThieves>=SimulConsts.E && (rooms[0]<0 || rooms[1]<0)) return 1;
        if(rooms[0]>=0 || rooms[1]>=0) return 2;

        while (waitingThieves<SimulConsts.E) {
            try {  wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }


    /**
     * The master thief prepares an assault party to lauch in excursion
     * 
     * @param ap assault party to prepare
     * @param room to assault
     */
    public synchronized void prepareAssaultParty(int ap, int room) {
        preparingAP = ap;
        while (excursion < SimulConsts.E) {
            try {
                summon = thievesQueue.read();
                waitingThieves--;
            } catch (Exception e) {}
            notifyAll();

            try { wait();
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }
        }
        excursion -= SimulConsts.E;
        preparingAP = -1;
        rooms[ap] = room;

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

        //if(ap0<SimulConsts.E && !heist0){ ap0++; return 0; } 
        //if(ap1<SimulConsts.E && !heist1){ ap1++; return 1; }
        if(preparingAP==0) ap0++; else ap1++;
        return preparingAP;
    }




    /**
     * The ordinary thief indicates to the master that he is available
     * 
     * @param ap assault party from which the ordinary thieve work before
     * @return master service decision
     */
    public synchronized boolean amINeeded(int ap){
        if(ap==0)
            if(--ap0==0) rooms[ap]=-1;
        else if(ap==1)
            if(--ap1==0) rooms[ap]=-1;

        int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
        try {
            thievesQueue.write(ordinaryId);
            waitingThieves++;
        } catch (Exception e) {}
        notifyAll();


        while(summon!=ordinaryId || !results){
			try { wait();
			} catch (InterruptedException e) {
				GenericIO.writelnString(" "+e.getMessage());
                System.exit(0);
			}
		}


        //Update Ordinary state
		((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.CONCENTRATION_SITE);
		repos.setOrdinaryState(ordinaryId, ((Ordinary) Thread.currentThread()).getOrdinaryState());

        return summon==ordinaryId;
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
