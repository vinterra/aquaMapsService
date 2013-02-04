package org.gcube.application.aquamaps.aquamapsservice.impl.engine.statistical;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.SMComputationConfig;
import org.gcube.data.analysis.statisticalmanager.stubs.SMComputationRequest;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMEntries;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMInputEntry;
public class StatisticalModule {

	protected static GCUBELog logger= new GCUBELog(StatisticalModule.class);
	
	private StatisticalManagerFactory factory;
	private StatisticalManagerDataSpace dataSpace;
	
	private StatisticalModule(GCUBEScope scope) {
		logger.trace("Scope is "+scope);
		
		ScopeProvider.instance.set(scope.toString());

		factory = StatisticalManagerDSL.createStateful().build();
		dataSpace=StatisticalManagerDSL.dataSpace().build();
	}
	
	
	public String submitRequest(final SourceGenerationRequest comp)throws Exception{
		
		//Store reference in DB?		
		final String requestId=SourceGenerationRequestsManager.insertRequest(comp);
	
		Thread t=new Thread(){
//			@Override
//			public void run() {
//				try{
//					
//					//checks for generation sets
//					//for each set check existing/ generating source
//						//if found bind to request
//						//else insert source reference, submit generation and link both to request
//					
//					
//					
//					
//					SMComputationConfig config=new SMComputationConfig();
//					
//					switch(comp.getLogic()){
//					case HCAF : config.setAlgorithm(comp.getAlgorithm());
//					break;
//					case HSPEC : config.setAlgorithm(comp.getAlgorithm());
//					break;
//					case HSPEN : config.setAlgorithm(comp.getAlgorithm());
//					break;
//					default : throw new ConfigurationException("Unable to set algorithm "+comp.getLogic());
//					}
//					
//					List<SMInputEntry> entries=new ArrayList<SMInputEntry>();
//					for(Entry<String,String> param:comp.getEnvironmentConfiguration().entrySet()){
//						entries.add(new SMInputEntry(param.getKey(), param.getValue()));
//					}
//					config.setParameters(new SMEntries(entries.toArray(new SMInputEntry[entries.size()])));
//					
//				}catch(Exception e){
//					SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.error, id);
//				}
//			}
		};
		return requestId;
	}
	
	
	private String submitTableGenerationRequest(SMComputationConfig config,String description,String title, String author)throws Exception{
		SMComputationRequest request=new SMComputationRequest(); 
		request.setConfig(config);
		request.setDescription(description);
		request.setTitle(title);
		request.setUser(author);
		return factory.executeComputation(request);
	}
}
