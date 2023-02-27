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

    private int [] ordinaryState;


    /**
     *   Instantiation of a general repository object.
     *
     *     @param logFileName name of the logging file
     */

    public GeneralRepos (String logFileName)
    {
        if ((logFileName == null) || Objects.equals (logFileName, ""))
            this.logFileName = "logger";
        else this.logFileName = logFileName;
        masterState = MasterStates.;                // init master state
        ordinaryState = new int[SimulConsts.O];     // init ordinary state
        for (int i = 0; i < SimulConsts.O; i++)
            ordinaryState[i] = OrdinaryStates.;
        reportInitialStatus ();
    }

    /**
     *   Set master state.
     *
     *     @param state master state
     */

    public synchronized void setMasterState ( int state)
    {
        switch (state){
            case MasterStates.WAITING_FOR_AN_ORDER:
                printText("Master state: Waiting for an order!");
                break;
            case MasterStates.PREPARING_THE_COURSE:
                printText("Master state: Preparing the corse!");
                break;
            case MasterStates.DISHING_THE_PORTIONS:
                printText("Master state: Dishing the portions!");
                break;
            case MasterStates.DELIVERING_THE_PORTIONS:
                printText("Master state: Delivering the portions!");
                break;
            case MasterStates.CLOSING_SERVICE:
                printText("Master state: Closing service!");
                break;
        }
        masterState = state;
        reportStatus();
    }



    /**
     *   Set ordinary state.
     *
     *     @param id ordinary id
     *     @param state ordinary state
     */

    public synchronized void setOrdinaryState (int id, int state)
    {
        switch (state){
            case OrdinaryStates.APPRAISING_SITUATION:
                printText("Ordinary state: Appraising situation!");
                break;
            case OrdinaryStates.PRESENTING_THE_MENU:
                printText("Ordinary state: Presenting the menu!");
                break;
            case OrdinaryStates.TAKING_THE_ORDER:
                printText("Ordinary state: Taking the order!");
                break;
            case OrdinaryStates.PLACING_THE_ORDER:
                printText("Ordinary state: Placing the order!");
                break;
            case OrdinaryStates.WAITING_FOR_PORTION:
                printText("Ordinary state: Waiting for portion!");
                break;
            case OrdinaryStates.PROCESSING_THE_BILL:
                printText("Ordinary state: Processing the bill!");
                break;
            case OrdinaryStates.RECEIVING_PAYMENT:
                printText("Ordinary state: Receiving payment!");
                break;
        }
        ordinaryState[id] = state;
        reportStatus ();
    }

    /**
     *   Set master state.
     *
     *     @param state master state
     */

    public synchronized void setMasterState ( int state)
    {
        switch (state){
            case MasterStates.APPRAISING_SITUATION:
                printText("Master state: Appraising situation!");
                break;
            case MasterStates.PRESENTING_THE_MENU:
                printText("Master state: Presenting the menu!");
                break;
            case MasterStates.TAKING_THE_ORDER:
                printText("Master state: Taking the order!");
                break;
            case MasterStates.PLACING_THE_ORDER:
                printText("Master state: Placing the order!");
                break;
            case MasterStates.WAITING_FOR_PORTION:
                printText("Master state: Waiting for portion!");
                break;
            case MasterStates.PROCESSING_THE_BILL:
                printText("Master state: Processing the bill!");
                break;
            case MasterStates.RECEIVING_PAYMENT:
                printText("Master state: Receiving payment!");
                break;
        }
        masterState = state;
        reportStatus();
    }


    /**
     *  Write the header to the logging file.
     *
     *  The barbers are sleeping and the customers are carrying out normal duties.
     *  Internal operation.
     */

    private void reportInitialStatus ()
    {
        TextFile log = new TextFile ();                      // instantiation of a text file handler

        if (!log.openForWriting (".", logFileName)) {
            GenericIO.writelnString("The operation of creating the file " + logFileName + " failed!");
            System.exit(1);
        }

        log.writelnString ( "                           The Restaurant - Description of the internal state");
        log.writelnString ("  Master Master Stu0 Stu1 Stu2 Stu3 Stu4 Stu5 Stu6 NCourse NPortion                         Table\n");
        if (!log.close ())
        { GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
        reportStatus ();
    }


    /**
     *  Write a state line at the end of the logging file.
     *
     *  The current state of entities is organized in a line to be printed.
     *
     */

    private void reportStatus ()
    {
        TextFile log = new TextFile ();                      // instantiation of a text file handler

        String lineStatus = "";                              // state line to be printed

        if (!log.openForAppending (".", logFileName))
        { GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
            System.exit (1);
        }

        switch (masterState){
            case MasterStates.WAITING_FOR_AN_ORDER:
                lineStatus += " WAFOR ";
                break;
            case MasterStates.PREPARING_THE_COURSE:
                lineStatus += " PRPCS ";
                break;
            case MasterStates.DISHING_THE_PORTIONS:
                lineStatus += " DSHPT ";
                break;
            case MasterStates.DELIVERING_THE_PORTIONS:
                lineStatus += " DLVPT ";
                break;
            case MasterStates.CLOSING_SERVICE:
                lineStatus += " CLSSV ";
                break;
        }

        for (int i = 0; i < SimulConsts.O; i++)
            switch (ordinaryState[i]) {
                case OrdinaryStates.GOING_TO_THE_RESTAURANT:
                    lineStatus += " GGTRT ";
                    break;
                case OrdinaryStates.TAKING_A_SEAT_AT_THE_TABLE:
                    lineStatus += " TKSTT ";
                    break;
                case OrdinaryStates.SELECTING_THE_COURSES:
                    lineStatus += " SELCS ";
                    break;
                case OrdinaryStates.ORGANIZING_THE_ORDER:
                    lineStatus += " OGODR ";
                    break;
                case OrdinaryStates.CHATTING_WITH_COMPANIONS:
                    lineStatus += " CHTWC ";
                    break;
                case OrdinaryStates.ENJOYING_THE_MEAL:
                    lineStatus += " EJYML ";
                    break;
                case OrdinaryStates.PAYING_THE_MEAL:
                    lineStatus += " PYTBL ";
                    break;
                case OrdinaryStates.GOING_HOME:
                    lineStatus += " GGHOM ";
                    break;
            }

        lineStatus += " "+String.format("%2d", nCourses)+"  "+String.format("%2d", nPortions)+"\t";
        for(int i = 0; i< SimulConsts.N; i++){
            if (nSeats[i] == -1)
                lineStatus += " ";
            lineStatus += " "+ nSeats[i]+"\t";
        }
        log.writelnString (lineStatus);
        if (!log.close ())
        { GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }


    /**
     *   Write to the logging file a message.
     *
     *     @param msg String
     *     Auxiliary function
     */

    private void printText(String msg)
    {
        TextFile log = new TextFile ();

        if (!log.openForAppending (".", logFileName))
        { GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
            System.exit (1);
        }

        log.writelnString(msg);

        if (!log.close ())
        { GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }
    }
