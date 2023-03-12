package sharedRegions;

import commInfra.*;
import entities.*;
import genclass.*;
import main.SimulConsts;

public class AssaultParty {



    private int member;



    /**
     * Sended assault party
     */
    private boolean sended;

    /**
     * Crawl In initialization
     */
    private boolean crawlin;

    /**
     * Crawl In initialization
     */
    private boolean crawlout;

    /**
     * Distance form each member to room 
     */
    private int[] distRoom;

    /**
     * Distance form each member to site 
     */ 
    private int[] distSite;

    /**
     * Reverse signal
     */
    
     private boolean reversed;


    /**
     *   Reference to the general repository.
     */
    private final GeneralRepos repos;

    /**
     * Bar instantiation
     *
     * @param repos reference to the general repository
     */

     public AssaultParty (GeneralRepos repos){
        
        this.repos = repos;
        this.reversed = false;
        this.sended = false;
        this.crawlin = false;
        this.crawlout = false;
        this.distRoom = new int[SimulConsts.E];
        this.distSite = new int[SimulConsts.E];
    }
    

    public synchronized void reverseDirection(int member){
        crawlout = false;
        reversed = true;
        if(member == 2 ) notifyAll();

        //Update Ordinary state
        int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
		((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.CRAWLING_OUTWARDS);
		repos.setOrdinaryState(ordinaryId, ((Ordinary) Thread.currentThread()).getOrdinaryState());

    }

    public synchronized int sendAssaultParty(){
        crawlin = false;
        sended = true;
        notifyAll();

        //Update Master state
		((Master) Thread.currentThread()).setMasterState(MasterStates.DECIDING_WHAT_TO_DO);
		repos.setMasterState(((Master) Thread.currentThread()).getMasterState());

        return 0;
    }


    public synchronized boolean crawlIn(int member, int md){
        int move; 

		while(!(crawlin && valid()) || !(member==0 && sended)){
			try { wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

        if(member==0 && sended){
            crawlin = true;
            sended = false;
        }

        do{
            move = 2 + (int)(Math.random() * (md - 2)+1);
        }while(!valid());

        distRoom[member]-=move;
        notifyAll();

        return distRoom[member]<=0;
    }




    public synchronized boolean crawlOut(int md){
        int move; 
        
        while(!(crawlout && valid()) || !(member==2 && reversed)){
			try { wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

        if(member==0 && reversed){
            crawlout = true;
            reversed = false;
        }

        do{
            move = 2 + (int)(Math.random() * (md - 2)+1);
        }while(!valid());

        distRoom[member]-=move;
        notifyAll();

        if(distSite[member]<=0){
            //Update Ordinary state
		    int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
		    ((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.COLLECTION_SITE);
		    repos.setOrdinaryState(ordinaryId, ((Ordinary) Thread.currentThread()).getOrdinaryState());
        }

        return distSite[member]<=0;
    }


    
    private boolean valid(){
        return true;
    }
    
}