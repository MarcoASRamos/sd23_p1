package main;

import sharedRegions.*;
import entities.*;

/**
 *   Simulation of the Assignment 1 - The Museum Simulation.
 *   Static solution Attempt (number of threads controlled by global constants - SimulConsts)
 */
public class MuseumSimulation{

    /**
     *    Main method.
     *
     *    @param args runtime arguments
     */
    public static void main(String[] args) {
        System.out.println("museum");
        
        Master master;				 //Reference to the Master thief Thread
        Ordinary[] ordinaries = new Ordinary[SimulConsts.M-1];    //References to the Ordinary thief Threads

        Museum museum;							    //Reference to the Museum
        AssaultParty[] party;					    //Reference to the Assault Parties
        ConcentrationSite cs;					    //Reference to the Concentration Site
        ControlCollectionSite ccs;					//Reference to the Control Collection Site
        GeneralRepos repos;                         //Reference to the General Repository

        System.out.println("The Restaurant Simulation");
        // problem initialization
        repos = new GeneralRepos("logger");
        int[] rooms = new int[SimulConsts.N];
        for(int i=0; i<SimulConsts.N; i++) 
            rooms[i] = SimulConsts.d +(int)(Math.random()* (SimulConsts.D-SimulConsts.d)+1);
        party = new AssaultParty[2];
        for(int i=0; i<2; i++)
            party[i] = new AssaultParty(repos, rooms);
        cs = new ConcentrationSite(repos);
        ccs = new ControlCollectionSite(repos);
        museum = new Museum(repos);

        master = new Master("Master", 0, MasterStates.PLANNING_THE_HEIST, repos, cs, ccs, party);
        for (int i = 0; i < SimulConsts.M-1; i++)
            ordinaries[i] = new Ordinary("Ordinary_"+(i+1), i, OrdinaryStates.CONCENTRATION_SITE, repos, cs, ccs, party, museum);
    
        // start of the simulation
        master.start();
        System.out.println("Master start");
        for (int i = 0; i < SimulConsts.M-1; i++) {
            ordinaries[i].start();
            //System.out.println("Ordinary "+ i +" start");
        }

        // waiting for the end of the simulation
        for (int i = 0; i < SimulConsts.M-1; i++){ 
            try{
                ordinaries[i].join ();
                System.out.println("Ordinary "+ i +" join");
            } catch (InterruptedException e) {}
            System.out.println("The Ordinary "+(i+1)+" just terminated");
        }

        try {
            master.join();
            System.out.println("Master join");
        } catch (InterruptedException e) {}

        repos.reportFinalStatus();  //mudar localizaçao???
        System.out.println("The Master has terminated");
        System.out.println("End of the Simulation");
    }
}
