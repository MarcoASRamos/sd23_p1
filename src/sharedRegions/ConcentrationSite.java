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
     * Excursion thieve to participate in a party
     */
    private int excursionId;

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
     * Number of members on Assault parties
     */
    private int[] party;

    /**
     * Indicate which the Assault party is heisting
     */
    private int rooms[];

    /**
     * Getter room assign to the assault party
     * 
     * @param ap assault party
     * @return room address to heist
     */
    public synchronized int getRoom(int ap){
        return rooms[ap];
    }

    /**
     * Return one assault party available
     * 
     * @return assault party
     */
    public synchronized int getAssautlParty(){
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
        this.excursionId = -1;
        this.results = false;
        this.summon = -1;
        this.preparingAP = -1;
        this.party = new int[2];
        this.rooms = new int[2];
        for(int i=0; i<2; i++){
            party[i] = 0;
            rooms[i] = -1; 
        } 
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
        int recruited = 0;

        while (recruited < SimulConsts.E) {
            if(excursionId==summon){
                recruited++;
                try {
                    summon = thievesQueue.read();
                    waitingThieves--;
                } catch (Exception e) {}
                notifyAll();
            } 

            try { wait();
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }
        }
        summon = -1;
        excursionId = -1;
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
        // Update Ordinary state
        int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
        ((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.CRAWLING_INWARDS);
        repos.setOrdinaryState(ordinaryId, ((Ordinary) Thread.currentThread()).getOrdinaryState());

        excursionId = ordinaryId;
        notifyAll();

        party[preparingAP]++;
        repos.setOrdinarySituation(ordinaryId, 'P');
        return preparingAP;
    }




    /**
     * The ordinary thief indicates to the master that he is available
     * 
     * @param ap assault party from which the ordinary thieve work before
     * @return master service decision
     */
    public synchronized boolean amINeeded(int ap){
        if(ap>=0 && --party[ap]==0) rooms[ap]=-1;

        int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
        repos.setOrdinarySituation(ordinaryId, 'W');
        try {
            thievesQueue.write(ordinaryId);
            waitingThieves++;
        } catch (Exception e) {}
        notifyAll();


        while(summon!=ordinaryId && !results){
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
        results = true;
        notifyAll();
        

        // Update Master state
        ((Master) Thread.currentThread()).setMasterState(MasterStates.PRESENTING_THE_REPORT);
        repos.setMasterState(((Master) Thread.currentThread()).getMasterState());

    }



}
