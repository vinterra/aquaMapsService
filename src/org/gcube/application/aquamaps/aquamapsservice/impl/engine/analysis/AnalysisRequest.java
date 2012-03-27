package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AnalysisType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;

public class AnalysisRequest {

	private AnalysisType toPerformAnalysis=null;
	
	private String[] hcafTables=null;
	private String[] hcafLabels=null;
	
	private String[] hspecTables=null;
	private String[] hspecLabels=null;
	
	private String[] hspenTables=null;
	private String[] hspenLabels=null;
	
	private String[] occurrenceTables=null;
	private String[] occurrenceLabels=null;
	
	private float hspecThreshold=0.8f; 
	
	private AnalysisWorker toNotify;
	public static final List<AnalysisRequest> getRequests(Analysis toPerform, AnalysisWorker toNotify)throws Exception{
		ArrayList<AnalysisRequest> toReturn=new ArrayList<AnalysisRequest>();
		List<Resource> sources=new ArrayList<Resource>();
		for(Integer id:toPerform.getSources())
			sources.add(SourceManager.getById(id));
		
		HashMap<ResourceType,HashSet<String>> sourceTables=new HashMap<ResourceType, HashSet<String>>();
		HashMap<ResourceType,ArrayList<String>> sourceLabels=new HashMap<ResourceType, ArrayList<String>>();
		for(Resource r:sources){
			if(!sourceTables.containsKey(r.getType())){
				sourceTables.put(r.getType(), new HashSet<String>());
				sourceLabels.put(r.getType(), new ArrayList<String>());
			}
			sourceTables.get(r.getType()).add(r.getTableName());
			sourceLabels.get(r.getType()).add(r.getTitle());
		}	
		
		
		String[] hcafTables=null;
		String[] hcafLabels=null;
		
		String[] hspecTables=null;
		String[] hspecLabels=null;
		
		String[] hspenTables=null;
		String[] hspenLabels=null;
		
		String[] occurrenceTables=null;
		String[] occurrenceLabels=null;
		
		
		
		
		
		for(ResourceType type:ResourceType.values()){
			if((sourceTables.containsKey(type)!=sourceLabels.containsKey(type))) 
				throw new Exception("Incoherent labels/tables for "+type+", TABLES : "+sourceTables.keySet()+", LABELS : "+sourceLabels.keySet());
			else if(sourceTables.containsKey(type)){
				if(sourceTables.get(type).size()!=sourceLabels.get(type).size()) 
					throw new Exception("Incoherent labels/tables for "+type+", TABLES : "+sourceTables.get(type)+", LABELS : "+sourceLabels.get(type));
				if(sourceTables.get(type).size()<2) throw new Exception("Not enough sources for "+type);
				
				switch(type){
				case HCAF : 			hcafTables=sourceTables.get(type).toArray(new String[sourceTables.get(type).size()]);
										hcafLabels=sourceLabels.get(type).toArray(new String[sourceLabels.get(type).size()]);
										break;
				case HSPEN : 			hspenTables=sourceTables.get(type).toArray(new String[sourceTables.get(type).size()]);
										hspenLabels=sourceLabels.get(type).toArray(new String[sourceLabels.get(type).size()]);
										break;
				case HSPEC : 			hspecTables=sourceTables.get(type).toArray(new String[sourceTables.get(type).size()]);
										hspecLabels=sourceLabels.get(type).toArray(new String[sourceLabels.get(type).size()]);
										break;
				case OCCURRENCECELLS : 	occurrenceTables=sourceTables.get(type).toArray(new String[sourceTables.get(type).size()]);
										occurrenceLabels=sourceLabels.get(type).toArray(new String[sourceLabels.get(type).size()]);
										break;
				}
			}
		}
		
		for(AnalysisType type:toPerform.getType())
			toReturn.add(
					new AnalysisRequest(type, hcafTables, hcafLabels, hspecTables, hspecLabels, 
							hspenTables, hspenLabels, occurrenceTables, occurrenceLabels, 0.8f,toNotify));
		return toReturn;
	}
	
	
	
	private AnalysisRequest(AnalysisType toPerformAnalysis, String[] hcafTables,
			String[] hcafLabels, String[] hspecTables, String[] hspecLabels,
			String[] hspenTables, String[] hspenLabels,
			String[] occurrenceTables, String[] occurrenceLabels,
			float hspecThreshold, AnalysisWorker toNotify) {
		super();
		this.toPerformAnalysis = toPerformAnalysis;
		this.hcafTables = hcafTables;
		this.hcafLabels = hcafLabels;
		this.hspecTables = hspecTables;
		this.hspecLabels = hspecLabels;
		this.hspenTables = hspenTables;
		this.hspenLabels = hspenLabels;
		this.occurrenceTables = occurrenceTables;
		this.occurrenceLabels = occurrenceLabels;
		this.hspecThreshold = hspecThreshold;
		this.toNotify=toNotify;
	}



	public String[] getLabels(ResourceType type){
		switch(type){
		case HCAF:return hcafLabels;
		case HSPEC:return hspecLabels;
		case HSPEN:return hspenLabels;
		default : return occurrenceLabels;
		}
	}
	public String[] getTables(ResourceType type){
		switch(type){
		case HCAF:return hcafTables;
		case HSPEC:return hspecTables;
		case HSPEN:return hspenTables;
		default : return occurrenceTables;
		}
	}
	public AnalysisType getToPerformAnalysis() {
		return toPerformAnalysis;
	}
	public float getHspecThreshold() {
		return hspecThreshold;
	}
	public void notify(AnalysisResponseDescriptor descriptor,Analyzer analyzer){
		toNotify.notifyGenerated(descriptor, analyzer);
	}
}
