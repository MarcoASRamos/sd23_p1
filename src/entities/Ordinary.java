package entities;

import sharedRegions.*;

public class Ordinary extends Thread{

    /**
     *  Ordinary Name
     */
    private String name;

    /**
     * Ordinary Id
     */
    private int ordinaryId;

    /**
     * Ordinary State
     */
    private int ordinaryState;

    /**
     * Reference to the Assault Party 0
     */
    private final AssaultParty0 ap0;

    /**
     * Reference to the Assault Party 1
     */
    private final AssaultParty1 ap1;

    /**
     * Reference to the Concentration Site
     */
    private final ConcentrationSite cs;

    /**
     * Reference to the Control and Collection Site
     */
    private final ControlCollectionSite ccs;

    /**
     * Reference to the Museum
     */
    private final Museum museum;

    /**
     * Reference to the general repository
     */
    private final GeneralRepos repos;

    /**
     * Instantiation of a ordinary thread.
     * @param name  ordinary Name
     * @param ordinaryId    ordinary Id
     * @param ordinaryState ordinary state
     * @param repos Reference to GeneralRepos
     */
    public Ordinary(String name, int ordinaryId, int ordinaryState, GeneralRepos repos, ConcentrationSite cs, ControlCollectionSite ccs, AssaultParty0 ap0, AssaultParty1 ap1, Museum museum) {
        this.name = name;
        this.ordinaryState = ordinaryState;
        this.ordinaryId = ordinaryId;
        this.ap0 = ap0;
        this.ap1 = ap1;
        this.cs = cs;
        this.ccs = ccs;
        this.museum = museum;
        this.repos = repos;
    }

    /**
     * Get ordinary name
     * @return ordinary name
     */
    public String getOrdinaryName() {
        return name;
    }

    /**
     * Set ordinary name
     * @param name ordinary name
     */
    public void setOrdinaryName(String name) {
        this.name = name;
    }

    /**
     * Get ordinary Id
     * @return ordinary Id
     */
    public int getOrdinaryId() {
        return ordinaryId;
    }

    /**
     * Set ordinary Id
     * @param ordinaryId ordinary Id
     */
    public void setOrdinaryId(int ordinaryId) {
        this.ordinaryId = ordinaryId;
    }

    /**
     * Get ordinary State
     * @return ordinary state
     */
    public int getOrdinaryState() {
        return ordinaryState;
    }

    /**
     * Set ordinary State
     * @param ordinaryState ordinary state
     */
    public void setOrdinaryState(int ordinaryState) {
        this.ordinaryState = ordinaryState;
    }

    /**
     * @return GeneralRepos
     */
    public GeneralRepos getRepos() {
        return repos;
    }

    /**
     * Life cycle of the ordinary.
     */
    @Override
    public void run() {
        int ap = ;
        while(cs.amINeeded()){
            int member = cs.prepareExcursion();
            

            boolean room = false;
            while (!room)
                room = ap==0? ap0.crawlIn(member): ap1.crawlIn(member);
            
            museum.rollACanvas();

            boolean site = false;
            while(!site){
                if (ap==0){
                    ap0.reverseDirection(member);
                    site = ap0.crawlOut(member);
                }else{
                    ap1.reverseDirection();
                    site = ap1.crawlOut(member);
                }
            }
            
            ccs.handACanvas();
        }

    }
    
}
