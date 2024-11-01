package entities;

import main.SimulConsts;
import sharedRegions.*;

public class Ordinary extends Thread {

    /**
     * Ordinary Name
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
     * Reference to the Museum
     */
    private final Museum museum;

    /**
     * Reference to the general repository
     */
    private final GeneralRepos repos;

    /**
     * Instantiation of a ordinary thread.
     * 
     * @param name          ordinary Name
     * @param ordinaryId    ordinary Id
     * @param ordinaryState ordinary state
     * @param repos         Reference to GeneralRepos
     */
    public Ordinary(String name, int ordinaryId, int ordinaryState, GeneralRepos repos, ConcentrationSite cs,
            ControlCollectionSite ccs, AssaultParty[] party, Museum museum) {
        this.name = name;
        this.ordinaryState = ordinaryState;
        this.ordinaryId = ordinaryId;
        this.party = party;
        this.cs = cs;
        this.ccs = ccs;
        this.museum = museum;
        this.repos = repos;
    }

    /**
     * Get ordinary name
     * 
     * @return ordinary name
     */
    public String getOrdinaryName() {
        return name;
    }

    /**
     * Set ordinary name
     * 
     * @param name ordinary name
     */
    public void setOrdinaryName(String name) {
        this.name = name;
    }

    /**
     * Get ordinary Id
     * 
     * @return ordinary Id
     */
    public int getOrdinaryId() {
        return ordinaryId;
    }

    /**
     * Set ordinary Id
     * 
     * @param ordinaryId ordinary Id
     */
    public void setOrdinaryId(int ordinaryId) {
        this.ordinaryId = ordinaryId;
    }

    /**
     * Get ordinary State
     * 
     * @return ordinary state
     */
    public int getOrdinaryState() {
        return ordinaryState;
    }

    /**
     * Set ordinary State
     * 
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
        int memberId, room, canvas, ap = -1, md = 2 + (int) (Math.random() * (SimulConsts.MD - 2) + 1);
        repos.setOrdinariesMD(ordinaryId, md);

        while (cs.amINeeded(ap)) {
            ap = cs.prepareExcursion();

            memberId = party[ap].assignMember(ap);
            boolean atRoom = true;
            while (atRoom)
                atRoom = party[ap].crawlIn(ap, memberId, md);

            room = party[ap].getRoom();
            canvas = museum.rollACanvas(room, ap, memberId);

            memberId = party[ap].assignMember(ap);
            party[ap].reverseDirection(memberId);
            boolean atSite = true;
            while (atSite)
                atSite = party[ap].crawlOut(ap, memberId, md);

            ccs.handACanvas(canvas, cs.getRoom(ap), ap, memberId);
        }
    }

}
