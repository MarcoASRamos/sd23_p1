package sharedRegions;

import main.*;
import entities.*;
import genclass.GenericIO;
import genclass.TextFile;
import java.util.Objects;

public class GeneralRepos {
    /**
     *  Name of the logging file.
     */

    private final String logFileName;

    /**
     *  State of the Master.
     */

    private int masterState;

    /**
     *  State of the ordinaries.
     */

    private int[] ordinaryState;

    /**
     * Situation of the ordinaries.
     */
    private char[] ordinarySituation;

    /**
     * Maximum displacement of the ordinaries.
     */
    private int[] MD;




    /**
     * Rooms number of paitngs hanging on the walls
     */
    private int[] paintings;

    /**
     * Rooms distance from outside gathering site
     */
    private int[] distances;





    /**
     * 
     */
    


    /**
     * 
     */


    /**
     * 
     */

    /**
     * 
     */


    /**
     * 
     */




    /**
     *   Instantiation of a general repository object.
     *
     *    @param logFileName name of the logging file
     */

    public GeneralRepos (String logFileName) {
        if ((logFileName == null) || Objects.equals (logFileName, ""))
            this.logFileName = "logger";
        else this.logFileName = logFileName;

        //master thieve
        masterState = MasterStates.PLANNING_THE_HEIST;

        //ordinarie thives
        ordinaryState = new int[SimulConsts.M-1];
        ordinarySituation = new char[SimulConsts.M-1];
        for (int i = 0; i < SimulConsts.M-1; i++){
            ordinaryState[i] = OrdinaryStates.CONCENTRATION_SITE;
        }

        //museum
        paintings = new int[SimulConsts.N];
        distances = new int[SimulConsts.N];

        //assault party 0


        //assult party 1


        reportInitialStatus ();
    }

    /**
     *   Set master state.
     *
     *     @param state master state
     */

    public synchronized void setMasterState ( int state) {
        masterState = state;
        reportStatus();
    }



    /**
     *   Set ordinary state.
     *
     *     @param id ordinary id
     *     @param state ordinary state
     */

    public synchronized void setOrdinaryState (int id, int state) {
        ordinaryState[id] = state;
        reportStatus ();
    }


    /**
     * 
     */
    public synchronized void 

    /**
     *  Write the header to the logging file.
     *
     *  The barbers are sleeping and the customers are carrying out normal duties.
     *  Internal operation.
     */

    private void reportInitialStatus () {
        TextFile log = new TextFile ();                      // instantiation of a text file handler

        if (!log.openForWriting (".", logFileName)) {
            GenericIO.writelnString("The operation of creating the file " + logFileName + " failed!");
            System.exit(1);
        }

        log.writelnString ( "                           Heist to the Museum - Description of the internal state\n");
        log.writelnString ("  Mstr   Thief 2     Thief 3     Thief 4     Thief 5     Thief 6");
        log.writelnString ("Stat    Stat S MD   Stat S MD   Stat S MD   Stat S MD   Stat S MD");
        log.writelnString ("                Assault party 1                                 Assault party 2                             Museum");
        log.writelnString ("    Elem 1          Elem 2          Elem 3          Elem 1          Elem 2          Elem 3      Room 1   Room 2   Room 3   Room 4   Room 5");
        log.writelnString ("RId Id Pos Cv   RId Id Pos Cv   RId Id Pos Cv   RId Id Pos Cv   RId Id Pos Cv   RId Id Pos Cv   NP DT    NP DT    NP DT    NP DT    NP DT");

        
        if (!log.close ()) { 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
        reportStatus ();
    }


    /**
     * 
     */

    private void reportFinalStatus (){
        TextFile log = new TextFile ();

        if (!log.openForWriting (".", logFileName)) {
            GenericIO.writelnString("The operation of creating the file " + logFileName + " failed!");
            System.exit(1);
        }

        log.writelnString ( "My friends, tonight's effort produced "++" priceless paintings!");

        if (!log.close ()) { 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }

    /**
     *  Write a state line at the end of the logging file.
     *
     *  The current state of entities is organized in a line to be printed.
     *
     */

    private void reportStatus () {
        TextFile log = new TextFile ();     // instantiation of a text file handler

        String lineStatus = "";             // state line to be printed

        if (!log.openForAppending (".", logFileName)) { 
            GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
            System.exit (1);
        }

        switch (masterState){
            case MasterStates.PLANNING_THE_HEIST:
                lineStatus += " PTH ";
                break;
            case MasterStates.DECIDING_WHAT_TO_DO:
                lineStatus += " DWTD ";
                break;
            case MasterStates.ASSEMBLING_A_GROUP:
                lineStatus += " AAG ";
                break;
            case MasterStates.WAITING_FOR_GROUP_ARRIVAL:
                lineStatus += " WFGA ";
                break;
            case MasterStates.PRESENTING_THE_REPORT:
                lineStatus += " PTR ";
                break;
        }

        for (int i = 0; i < SimulConsts.M-1; i++)
            switch (ordinaryState[i]) {
                case OrdinaryStates.CONCENTRATION_SITE:
                    lineStatus += " CONS "+++;
                    break;
                case OrdinaryStates.CRAWLING_INWARDS:
                    lineStatus += " CIN "+++;
                    break;
                case OrdinaryStates.AT_A_ROOM:
                    lineStatus += " AAR "+++;
                    break;
                case OrdinaryStates.CRAWLING_OUTWARDS:
                    lineStatus += " COUT "+++;
                    break;
                case OrdinaryStates.COLLECTION_SITE:
                    lineStatus += " COLS "+++;
                    break;
            }

        
        log.writelnString (lineStatus);

        
        log.writelnString("");


        if (!log.close ()) { 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }


    /**
     *   Write to the logging file a message.
     *
     *     @param msg String
     *     Auxiliary function
     */

    private void printText(String msg) {
        TextFile log = new TextFile ();

        if (!log.openForAppending (".", logFileName)) { 
            GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
            System.exit (1);
        }

        log.writelnString(msg);

        if (!log.close ()) { 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }
}
