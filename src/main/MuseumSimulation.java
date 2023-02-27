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

        Master master;										    //Reference to the Master thief Thread
        Ordinary[] ordinaries = new Ordinary[SimulConsts.O];    //array of references to the Ordinary thief Threads

        Museum museum;							    //Reference to the Museum
        AssaultParty0 ap0;					        //Reference to the Assault Party #0
        AssaultParty1 ap1;						    //Reference to the Assault Party #1
        ConcentrationSite cs;					    //Reference to the Concentration Site
        ControlCollectionSite ccs;					//Reference to the Control Collection Site
        GeneralRepos repos;                         //Reference to the General Repository

        System.out.println("The Restaurant Simulation");
        /* problem initialization */
        repos = new GeneralRepos("logger");
        ap0 = new AssaultParty0(repos);
        ap1 = new AssaultParty1(repos);
        cs = new ConcentrationSite(repos);
        ccs = new ControlCollectionSite(repos);

        master = new Master("Master", 0, MasterStates.PLANNING_THE_HEIST, repos);
        for (int i = 0; i < SimulConsts.M-1; i++)
            ordinaries[i] = new Ordinary("Ordinary_"+(i+1), i, OrdinaryStates.CONCENTRATION_SITE, repos);
    
        /* start of the simulation */
        master.start();
        System.out.println("Master start");
        for (int i = 0; i < SimulConsts.M-1; i++) {
            ordinaries[i].start();
            System.out.println("Ordinary "+ i +" start");
        }
        /* waiting for the end of the simulation */
        for (int i = 0; i < SimulConsts.M-1; i++)
        { try
        {
            ordinaries[i].join ();
            System.out.println("Ordinary "+ i +" join");
        }
        catch (InterruptedException e) {}
            System.out.println("The Ordinary "+(i+1)+" just terminated");
        }

        try {
            master.join();
            System.out.println("Master join");
        } catch (InterruptedException e) {}
        System.out.println("The Master has terminated");

        System.out.println("End of the Simulation");
    }
}
