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
     * Indicate if the thieve gives a canvas or is empty
     */
    private int canvas;

    /**
     * Indicate master thieve as collected the canvas
     */
    private boolean collected;

    /**
     * Indicate the room assaulted
     */
    private int room;

    /**
     * State of each room, if is full or not
     */
    private boolean[] rooms;

    /**
     * Index of rooms still with paintings
     */
    private int idx;

    /**
     * Getter room state list
     * 
     * @return room state list
     */
    public synchronized int getRoomIdx(){
        while(idx<SimulConsts.N){
            if(rooms[idx]) break;
            idx++;
        }
        return idx;
    }

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
        this.canvas = -1;
        this.room = -1;
        this.idx = 0;
        this.rooms = new boolean[SimulConsts.N];
        for(int i=0; i<SimulConsts.N; i++) rooms[i]=true;
        this.handed = false;
        this.collected = false;
    }

    


    /**
     * Master starts the heist the museum operation
     */
    public synchronized void startOperation(){
        //Update Master state
		((Master) Thread.currentThread()).setMasterState(MasterStates.DECIDING_WHAT_TO_DO);
		repos.setMasterState(((Master) Thread.currentThread()).getMasterState());
    }


    /**
     * Master hide until return of the ordinaries 
     */
    public synchronized void takeARest(){

        while(!handed){
			try { wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

        if(canvas==0) rooms[room] = false;
        else repos.setRobbedPaintings();
        
        canvas = -1; 
        room = -1;

        //Update Master state
		((Master) Thread.currentThread()).setMasterState(MasterStates.WAITING_FOR_GROUP_ARRIVAL);
		repos.setMasterState(((Master) Thread.currentThread()).getMasterState());

    }



    /**
     * Ordinary thieves takes the canvas out of the cylinder and hands it to the master thief, or tells her he is coming empty-handed
     * 
     * @param canvas or empty handed
     * @param ap assault party
     * @param members member id
     * @param room heisted by the thief
     */
    public synchronized void handACanvas(int canvas, int room, int ap, int members){
        
        while(handed){
			try { wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

        handed = true;
        this.canvas = canvas;
        this.room = room;
        notifyAll();
        repos.setCanvas(ap * SimulConsts.E + members, 0);

        while(!collected){
			try { wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

        collected = false;
    }




    /**
     * The master thief stores it in the back of a van
     */
    public synchronized void collectACanvas(){
        collected = true;
        handed = false;
        notifyAll();
        
        //Update Master state
		((Master) Thread.currentThread()).setMasterState(MasterStates.DECIDING_WHAT_TO_DO);
		repos.setMasterState(((Master) Thread.currentThread()).getMasterState());
    }
    
}
