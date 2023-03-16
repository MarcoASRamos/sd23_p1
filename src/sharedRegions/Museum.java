package sharedRegions;

import commInfra.*;
import entities.*;
import genclass.*;
import main.SimulConsts;

public class Museum {

    /**
     * Number of paintings hanging in each room
     */
    private int[] paintings;

    /**
     * Reference to the general repository.
     */
    private final GeneralRepos repos;

    /**
     * Bar instantiation
     *
     * @param repos reference to the general repository
     */
    public Museum(GeneralRepos repos) {

        this.repos = repos;
        this.paintings = new int[SimulConsts.N];
        for(int i=0; i<SimulConsts.N; i++) 
            paintings[i] = SimulConsts.p +(int)(Math.random() * (SimulConsts.P-SimulConsts.p)+1); 
        repos.setRoomPaitings(paintings);
    }

    /**
     * The thieve detaches the canvas from the framing, 
     * rolls it over and inserts it in a cylinder container 
     * 
     * @param room where assault is happening
     * @return number of canvas stolen by the thieve
     */
    public synchronized int rollACanvas(int room) {
        if(paintings[room]>0) {
            paintings[room]--;
            repos.setRoomPaitings(paintings);
            return 1;
        }
        return 0;
    }

}
