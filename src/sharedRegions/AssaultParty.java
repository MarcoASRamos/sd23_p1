package sharedRegions;

import java.util.Arrays;
import commInfra.*;
import entities.*;
import genclass.*;
import main.SimulConsts;

public class AssaultParty {


    /**
     * Number of members in the party
     */
    private int members;

    /**
     * Getter/Assign an assault party id to the thieve 
     */
    public int assignMember(){
        members = (members+1)%SimulConsts.E;
        return members;
    }

    /**
     * Room to heist
     */
    private int room;

    /**
     * Getter room
     */
    public synchronized int getRoom(){
        return room;
    }

    /**
     * Sended assault party
     */
    private boolean sended;

    /**
     * Inital step for the first member of the assault party
     */
    private boolean init;
    
    /**
     * The movement of Crawl in has been initializated
     */
    private boolean crawlin;

    /**
     * Number of thieves who are at the room
     */
    private int atRoom;

    /**
     * The movement of Crawl out has been initializated
     */
    private boolean crawlout;

    /**
     * Positions of each member during crawl line 
     */
    private int[] pos;

    /**
     * Tha last member signals to reverse march
     */
    
     private boolean reversed;


    /**
     *   Reference to the general repository.
     */
    private final GeneralRepos repos;

    /**
     * Distaces in units from the site to the each museum room
     */
    private final int[] rooms;

    /**
     * Bar instantiation
     *
     * @param repos reference to the general repository
     */

     public AssaultParty (GeneralRepos repos, int[] rooms){
        
        this.repos = repos;
        this.rooms = rooms;
        System.out.println("Room 0 distance "+rooms[0]);
        this.room = -1;
        this.members = -1;
        this.reversed = false;
        this.sended = false;
        this.init = false;
        this.crawlin = false;
        this.atRoom = 0;
        this.crawlout = false;
        this.pos = new int[SimulConsts.E];
        for(int i=0; i<SimulConsts.E; i++) pos[i] = 0;
    }
    
    /**
     * 
     * @param member
     */
    public synchronized void reverseDirection(int member){
        crawlout = false;
        init = true;
        if(member==2){
            System.out.println("Reversed!");
            reversed = true; 
            atRoom=0;
            notifyAll();
        } 

        //Update Ordinary state
        int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
		((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.CRAWLING_OUTWARDS);
		repos.setOrdinaryState(ordinaryId, ((Ordinary) Thread.currentThread()).getOrdinaryState());

    }


    /**
     * The master sends the assault party to the museum to heist an especific room
     * 
     * @param room to heist
     */
    public synchronized void sendAssaultParty(int room){
        crawlin = false;
        sended = true;
        init = true;
        this.room=room;
        notifyAll();
        System.out.println("\nSended\n");

        //Update Master state
		((Master) Thread.currentThread()).setMasterState(MasterStates.DECIDING_WHAT_TO_DO);
		repos.setMasterState(((Master) Thread.currentThread()).getMasterState());
    }

    /**
     * The assault party get in line and crawl to the room of the museum
     * 
     * @param ap number
     * @param member id of the thieve in the crawl line
     * @param md maximum distance capable by the thive
     * @return true if the thieve get to the room
     */
    public synchronized boolean crawlIn(int ap, int member, int md){
        int move=0; 

        if(sended){
            for(int i=md; i>1; i--) {
                if(valid(member, i, ap)){
                    move = i; 
                    break;
                } 
            }
        }else{
            move = member==0?3:1;
        }
        
		while((crawlin && move<2) || (member!=0 && init) || !sended){
			try { wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

            for(int i=md; i>1; i--) {
                if(valid(member, i, ap)){
                    move = i; 
                    break;
                } 
            }
		}

        if(member==0 && init){
            crawlin = true;
            init = false;
        }

        pos[member]+=move;
        if(pos[member]>rooms[room]) pos[member] = rooms[room];

        int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
        repos.setPosition(ap*SimulConsts.E + member, pos[member]);
        notifyAll();

        

        if(pos[member]==rooms[room]){
            while(++atRoom < SimulConsts.E) {
                try { wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            notifyAll();
             
            //Update Ordinary state
		    ((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.AT_A_ROOM);
		    repos.setOrdinaryState(ordinaryId, ((Ordinary) Thread.currentThread()).getOrdinaryState());
        }

        return pos[member]<rooms[room];
    }



    /**
     * The assault party get in line and crawl back to the site
     * 
     * @param ap number
     * @param member id of the thieve in the crawl line
     * @param md maximum distance capable by the thive
     * @return true if the thieve get to the site
     */
    public synchronized boolean crawlOut(int ap, int member, int md){
        int move=0; 

        if(reversed){
            for(int i=md; i>1; i--) {
                if(valid(member, -i, ap)){
                    move = i; 
                    break;
                } 
            }
        }else{
            move = member==0?3:1;
        }


        System.out.println("member "+member+", positions "+pos[member]+" crawl "+crawlout+ ", move "+move+", init "+init+", reversed "+reversed);
        while((crawlout && move<2) || (member!=0 && init) || !reversed){
            System.out.println("member "+member+" is crawlout waiting");
			try { wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

            for(int i=md; i>1; i--) {
                if(valid(member, -i, ap)){
                    move = i; 
                    break;
                } 
            }
                 
		}

        if(member==0 && init){
            crawlout = true;
            init = false;
        }

        pos[member]-=move;
        if(pos[member]<0) pos[member]=0;

        int ordinaryId = ((Ordinary) Thread.currentThread()).getOrdinaryId();
        repos.setPosition(ap*SimulConsts.E + member, pos[member]);
        notifyAll();

        if(pos[member]==0){
            //Update Ordinary state
		    ((Ordinary) Thread.currentThread()).setOrdinaryState(OrdinaryStates.COLLECTION_SITE);
		    repos.setOrdinaryState(ordinaryId, ((Ordinary) Thread.currentThread()).getOrdinaryState());
        }

        return pos[member]>0;
    }




    private synchronized boolean valid(int member, int p, int ap){
        int[] test = pos.clone();
        test[member] += p;
        if(test[member]<0) test[member]=0;
        if(test[member]>rooms[room]) test[member]=rooms[room];

        Arrays.sort(test);
        System.out.printf("party %d positions %d %d %d\n",ap, test[0],test[1],test[2]);

        for(int i=SimulConsts.E-1; i>0; i--) {
            if(test[i-1]-test[i]==0 && (test[i]==0 || test[i]==rooms[room])) continue;
            if(test[i]-test[i-1]>SimulConsts.S || test[i-1]-test[i]==0) return false;
        }

        return true;
    }
}