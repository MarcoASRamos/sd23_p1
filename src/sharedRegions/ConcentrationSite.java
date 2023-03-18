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
     * Counter of summoned thieves
     */
    private int recruited;

    /**
     * Master is going to sum up the results 
     */
    private boolean results;

    /**
     * Summon thieve to an assault party
     */
    private int summon;

    /**
     * The thief summoned responds to the call
     */
    private boolean summoned;

    /**
     * Preparingassault Party
     */
    private int preparingAP;

    /**
     * Number of members on Assault parties
     */
    private int[] party;

    /**
     * Number of thieves currently assault the museum
     */
    private int heisting;

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
        this.results = false;
        this.recruited = 0;
        this.summoned = true;
        this.summon = -1;
        this.preparingAP = -1;
        this.heisting = 0;
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
        if(heisting>0){
            heisting--;
            return 2;
        } 

        while (waitingThieves<SimulConsts.E) {
            System.out.println("ApraiseSit waiting");
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

        while (recruited < SimulConsts.E) {
            if(summoned){
                summoned = false;
                try {
                    summon = thievesQueue.read();
                    waitingThieves--;
                } catch (Exception e) {}
                notifyAll();
            } 

            System.out.println("PrepareAP waiting");
            try { wait();
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }
        }

        recruited = 0;
        summon = -1;
        preparingAP = -1;
        rooms[ap] = room;
        heisting += SimulConsts.E;

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

        recruited++;
        summoned = true;
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
            System.out.println("ord "+ordinaryId+": amIneeded waiting");
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
