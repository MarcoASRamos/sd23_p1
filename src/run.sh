clear

javac -cp genclass.jar main/SimulConsts.java commInfra/MemException.java commInfra/MemObject.java commInfra/MemFIFO.java commInfra/package-info.java entities/MasterStates.java entities/Master.java entities/OrdinaryStates.java entities/Ordinary.java entities/package-info.java sharedRegions/AssaultParty.java sharedRegions/ConcentrationSite.java sharedRegions/ControlCollectionSite.java sharedRegions/GeneralRepos.java sharedRegions/Museum.java sharedRegions/package-info.java main/MuseumSimulation.java main/package-info.java

for i in $(seq 1 1000)
do
echo -e "\nRun n.o " $i
	java -cp .:genclass.jar main.MuseumSimulation 
done

