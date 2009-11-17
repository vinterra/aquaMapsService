package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.application.aquamaps.stubs.Specie;

public class JobGenerationDetails {

	public static final String Biodiversity="Biodiversity";
	public static final String SuitableArea="SuitableArea";
	
	public enum SpeciesStatus{
		toCustomize,toGenerate,Ready
	}
	
	public enum Status {
		Pending,Simulating,Generating,Publishing,Completed,Error
		}
	private Connection connection;
	private Map<Integer,Status> toPerformBiodiversity=new HashMap<Integer, Status>();
	private Map<Integer,Status> toPerformDistribution=new HashMap<Integer, Status>();
	private Map<String,SpeciesStatus> speciesHandling=new HashMap<String, SpeciesStatus>();
	private boolean areaReady=false;
	private Status status;
	private String hspenTable;
	private String hspecTable;
	private String hcafTable;	
	private Job toPerform;
	public Map<Integer, Status> getToPerformBiodiversity() {
		return toPerformBiodiversity;
	}
	public void setToPerformBiodiversity(Map<Integer, Status> toPerformBiodiversity) {
		this.toPerformBiodiversity = toPerformBiodiversity;
	}
	public Map<Integer, Status> getToPerformDistribution() {
		return toPerformDistribution;
	}
	public void setToPerformDistribution(Map<Integer, Status> toPerformDistribution) {
		this.toPerformDistribution = toPerformDistribution;
	}
	public Map<String, SpeciesStatus> getSpeciesHandling() {
		return speciesHandling;
	}
	public void setSpeciesHandling(Map<String, SpeciesStatus> speciesHandling) {
		this.speciesHandling = speciesHandling;
	}
	public boolean isAreaReady() {
		return areaReady;
	}
	public void setAreaReady(boolean areaReady) {
		this.areaReady = areaReady;
	}
	public String getHspenTable() {
		return hspenTable;
	}
	public void setHspenTable(String hspenTable) {
		this.hspenTable = hspenTable;
	}
	public String getHspecTable() {
		return hspecTable;
	}
	public void setHspecTable(String hspecTable) {
		this.hspecTable = hspecTable;
	}
	public String getHcafTable() {
		return hcafTable;
	}
	public void setHcafTable(String hcafTable) {
		this.hcafTable = hcafTable;
	}
	public Job getToPerform() {
		return toPerform;
	}
	public void setToPerform(Job toPerform) {
		this.toPerform = toPerform;
	}
	public JobGenerationDetails(Job toPerform) {
		this.toPerform=toPerform;
		//init Aquamap Status List
		for(int i=0;i<toPerform.getAquaMapList().getAquaMapList().length;i++){
			AquaMap obj=toPerform.getAquaMapList().getAquaMapList(i);
			if(obj.getType().toString().equalsIgnoreCase(Biodiversity))
				toPerformBiodiversity.put(i, Status.Pending);
			else toPerformDistribution.put(i, Status.Pending);
		}
		//init SpeciesHandling 
		for(Specie spec:toPerform.getSelectedSpecies().getSpeciesList())
			speciesHandling.put(spec.getId(), SpeciesStatus.toCustomize);
		
		status=Status.Pending;
	}
	public void setStatus(Status status) throws IOException, Exception {		
		JobUtils.updateStatus(this, connection,status);
		this.status = status;
	}
	public Status getStatus() {
		return status;
	}
	public String getFirstLevelDirName(){
		return ((toPerform.getHspec()!=null)?toPerform.getHspec().getName():"onTheFly");
	}
	public String getSecondLevelDirName(){
		return (toPerform.getName());
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setAquaMapStatus(Status status,int index) throws Exception{
		JobUtils.updateAquaMapStatus(this, toPerform.getAquaMapList().getAquaMapList(index), connection,status);		
		if(toPerformBiodiversity.containsKey(index)) toPerformBiodiversity.put(index, status);
		else toPerformDistribution.put(index, status);
	}
	public boolean isSpeciesListReady(List<String> speciesIdList){
		for(String id:speciesIdList){
			if(!speciesHandling.get(id).equals(SpeciesStatus.Ready)) return false;			
		}
		return true;
	}
}
