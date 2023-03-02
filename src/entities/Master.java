package entities;

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
     * Reference to the general repository
     */
    private final GeneralRepos repos;

    /**
     * Instantiation of a master thread.
     * 
     * @param name        master Name
     * @param masterId    master Id
     * @param masterState master state
     * @param ap0         Reference to AssaultParty0
     * @param ap1         Reference to AssaultParty1
     * @param cs          Reference to ConcentrationSite
     * @param ccs         Reference to ControlCollectionSite
     * @param repos       Reference to GeneralRepos
     */
    public Master(String name, int masterId, int masterState, AssaultParty0 ap0, AssaultParty1 ap1,
            ConcentrationSite cs, ControlCollectionSite ccs, GeneralRepos repos) {
        this.name = name;
        this.masterState = masterState;
        this.masterId = masterId;
        this.ap0 = ap0;
        this.ap1 = ap1;
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
        startOperation();

        boolean assault = true;
        while (assault) {
            switch (cs.appraiseSit()) {
                case 'a':
                    int ap = cs.prepareAssaultParty();
                    if (ap)
                        ap0.sendAssaultParty();
                    else
                        ap1.sendAssaultParty();
                    break;

                case 'r':
                    ccs.takeARest();
                    ccs.collectACanvas();
                    break;

                case 's':
                    cs.sumUpResults();
                    assault = false;
                    break;
            }
        }

    }

}
