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
    private int[] MDs;


    /**
     * Assault party elements
     */
    private int[][] elements;


    /**
     * Rooms number of paintings hanging on the walls
     */
    private int[] paintings;

    /**
     * Rooms distance from outside gathering site
     */
    private int[] distances;


    /**
     * Number of robbed paintings
     */
    private int robbed;


    




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
        MDs = new int[SimulConsts.M-1];
        for (int i = 0; i < SimulConsts.M-1; i++){
            ordinaryState[i] = OrdinaryStates.CONCENTRATION_SITE;
            ordinarySituation[i] = 'W';
            MDs[i] = 0;
        }

        //museum
        paintings = new int[SimulConsts.N];
        distances = new int[SimulConsts.N];

        //assault parties
        elements = new int[SimulConsts.M-1][4];
        for (int i = 0; i < SimulConsts.M-1; i++)
            resetApElement(i);

        //robbed paintings
        robbed = 0;

        
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
     * Set number of paitings on the walls
     * @param paitings on each room
     */
    public synchronized void setRoomPaitings(int[] paitings){
        this.paintings = paitings;
    }

    /**
     * Update number of paitings on the walls
     * @param room of the museum
     * @param paitings on each room
     */
    public synchronized void updateRoomPaitings(int room, int paitings){
        this.paintings[room] = paitings;
    }


    /**
     * Set distance of the rooms
     * @param distances of each room
     */
    public synchronized void setRoomDistances(int[] distances){
        this.distances = distances;
    }


    /**
     * Set ordinary thieves maximum distances
     * @param id of the ordinary thieve
     * @param md of the ordinary thieve
     */
    public synchronized void setOrdinariesMD(int id, int md){
        this.MDs[id] = md;
    }

    /**
     * Set Assault Party element
     * 
     * @param elem index (= ap*SimulConsts.E+memberId)
     * @param room room to assault
     * @param oid ordinary thieve id
     */
    public synchronized void setApElement(int elem, int room, int tid){
        elements[elem][0] = room;
        elements[elem][1] = tid;
        elements[elem][2] = 0;
        elements[elem][3] = 0;
    }

    public synchronized void resetApElement(int elem){
        elements[elem][0] = -1;
        elements[elem][1] = -1;
        elements[elem][2] = -1;
        elements[elem][3] = 0;
    }

    /**
     * Update carrying a canvas 
     * 
     * @param elem index
     * @param canvas carry
     */
    public synchronized void updateCanvas(int elem, int canvas){
        elements[elem][3] = canvas;
    }


    /**
     * Update thieve position
     * 
     * @param elem index
     * @param pos actual position of the thieve in line
     */
    public synchronized void updatePosition(int elem, int pos){
        elements[elem][2] = pos;
    }



    /**
     * Set robbed paintings
     */
    public synchronized void setRobbedPaintings(){
        this.robbed++;
    }





    /**
     *  Write the header to the logging file.
     *
     *  The Ordinaries are sleeping and the master are decidingheist next operation.
     *  Internal operation.
     */

    private void reportInitialStatus () {
        TextFile log = new TextFile ();                      // instantiation of a text file handler

        if (!log.openForWriting (".", logFileName)) {
            GenericIO.writelnString("The operation of creating the file " + logFileName + " failed!");
            System.exit(1);
        }

        log.writelnString ( "                           Heist to the Museum - Description of the internal state\n");
        log.writelnString ("  Mstr   Thief 1     Thief 2     Thief 3     Thief 4     Thief 5     Thief 6");
        log.writelnString ("Stat    Stat S MD   Stat S MD   Stat S MD   Stat S MD   Stat S MD   Stat S MD");

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
     *  Write the footer to the logging file.
     */

    public void reportFinalStatus (){
        TextFile log = new TextFile ();

        if (!log.openForWriting (".", logFileName)) {
            GenericIO.writelnString("The operation of creating the file " + logFileName + " failed!");
            System.exit(1);
        }

        log.writelnString ( "My friends, tonight's effort produced "+robbed+" priceless paintings!");

        if (!log.close ()) { 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }

    /**
     *  Write a state line at the end of the logging file.
     *
     *  The current state of entities is organized in a line to be printed.
     */

    private void reportStatus () {
        TextFile log = new TextFile ();             // instantiation of a text file handler
        String lineStatus = "", line2Status = "";   // state line to be printed

        if (!log.openForAppending (".", logFileName)) { 
            GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
            System.exit (1);
        }

        switch (masterState){
            case MasterStates.PLANNING_THE_HEIST:
                lineStatus += "PTH ";
                break;
            case MasterStates.DECIDING_WHAT_TO_DO:
                lineStatus += "DWTD ";
                break;
            case MasterStates.ASSEMBLING_A_GROUP:
                lineStatus += "AAG ";
                break;
            case MasterStates.WAITING_FOR_GROUP_ARRIVAL:
                lineStatus += "WFGA ";
                break;
            case MasterStates.PRESENTING_THE_REPORT:
                lineStatus += "PTR ";
                break;
        }

        for (int i = 0; i < SimulConsts.M-1; i++)
            switch (ordinaryState[i]) {
                case OrdinaryStates.CONCENTRATION_SITE:
                    lineStatus += "      CNS "+ordinarySituation[i]+" "+MDs[i];
                    break;
                case OrdinaryStates.CRAWLING_INWARDS:
                    lineStatus += "    CIN "+ordinarySituation[i]+" "+MDs[i];
                    break;
                case OrdinaryStates.AT_A_ROOM:
                    lineStatus += "    AAR "+ordinarySituation[i]+" "+MDs[i];
                    break;
                case OrdinaryStates.CRAWLING_OUTWARDS:
                    lineStatus += "    COUT "+ordinarySituation[i]+" "+MDs[i];
                    break;
                case OrdinaryStates.COLLECTION_SITE:
                    lineStatus += "    CLS "+ordinarySituation[i]+" "+MDs[i];
                    break;
            }
            
        log.writelnString (lineStatus);     // states of thieves


        String.format("RId Id Pos Cv   RId Id Pos Cv   RId Id Pos Cv   RId Id Pos Cv   RId Id Pos Cv   RId Id Pos Cv   NP DT    NP DT    NP DT    NP DT    NP DT");
        for(int i=0; i<SimulConsts.M-1; i++)
            line2Status +=  elements[i][0]+" "+elements[i][1]+" "+elements[i][2]+" "+elements[i][3]+"      ";     

        for(int i=0; i<SimulConsts.N; i++)
            line2Status +=  paintings[i]+" "+distances[i]+"      ";

        log.writelnString(line2Status);   // status of shared regions


        if (!log.close ()) { 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }
}
