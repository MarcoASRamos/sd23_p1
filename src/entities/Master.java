package entities;

import main.SimulConsts;
import sharedRegions.*;

public class Master extends Thread {

    /**
     * Master Name
     */
    private String name;

    /**
     * Master Id
     */
    private int masterId;

    /**
     * Master State
     */
    private int masterState;

    /**
     * Reference to the Assault Party 
     */
    private final AssaultParty[] party;

    /**
     * Reference to the Concentration Site
     */
    private final ConcentrationSite cs;

    /**
     * Reference to the Control and Collection Site
     */
    private final ControlCollectionSite ccs;

    /**
     * Reference to the general repository
     */
    private final GeneralRepos repos;

    /**
     * Instantiation of a master thread.
     * 
     * @param name        master Name
     * @param masterId    master Id
     * @param masterState master state
     * @param party       Reference to AssaultParty
     * @param cs          Reference to ConcentrationSite
     * @param ccs         Reference to ControlCollectionSite
     * @param repos       Reference to GeneralRepos
     */
    public Master(String name, int masterId, int masterState, GeneralRepos repos, ConcentrationSite cs, ControlCollectionSite ccs, AssaultParty[] party) {
        this.name = name;
        this.masterState = masterState;
        this.masterId = masterId;
        this.party = party;
        this.cs = cs;
        this.ccs = ccs;
        this.repos = repos;
    }

    /**
     * Get master name
     * 
     * @return master name
     */
    public String getMasterName() {
        return name;
    }

    /**
     * Set master name
     * 
     * @param name master name
     */
    public void setMasterName(String name) {
        this.name = name;
    }

    /**
     * Get master Id
     * 
     * @return master Id
     */
    public int getMasterId() {
        return masterId;
    }

    /**
     * Set master Id
     * 
     * @param masterId master Id
     */
    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    /**
     * Get master State
     * 
     * @return master state
     */
    public int getMasterState() {
        return masterState;
    }

    /**
     * Set master State
     * 
     * @param masterState master state
     */
    public void setMasterState(int masterState) {
        this.masterState = masterState;
    }

    /**
     * @return GeneralRepos
     */
    public GeneralRepos getRepos() {
        return repos;
    }

    /**
     * Life cycle of the master.
     */
    @Override
    public void run() {
        int room = ccs.getRoomIdx();

        //System.out.println("startOpr");
        ccs.startOperation();
        
        boolean assault = true;
        while (assault) {
            System.out.println("AppraiseSit");
            room = ccs.getRoomIdx();
            switch (cs.appraiseSit(room>=SimulConsts.N)){
                case 1:
                    int ap = cs.getAssautlParty();
                    //System.out.println("PrepareAp "+ap);
                    cs.prepareAssaultParty(ap, room);
                    if(ap==0) System.out.println("SendAp "+ap);
                    if(ap==0) party[ap].sendAssaultParty(cs.getRoom(ap));
                    break;



                case 2:
                    System.out.println("Rest");
                    ccs.takeARest();
                    System.out.println("CollectACanvas");
                    ccs.collectACanvas();
                    break;



                case 3:
                    System.out.println("SumUpResults");
                    cs.sumUpResults();
                    assault = false;
                    break;
            }
        }

    }

}
