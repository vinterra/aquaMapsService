package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Generator;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.GISUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.impl.model.File;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube.application.aquamaps.publisher.impl.model.WMSContext;
import org.gcube.common.core.utils.logging.GCUBELog;

public class DeletionMonitor extends Thread {

	private static GCUBELog logger= new GCUBELog(DeletionMonitor.class);
	
	private long interval;
	
	final static Publisher publisher=ServiceContext.getContext().getPublisher();
	
	private static Generator<FileSet> fileSetDestroyer=new Generator<FileSet>(null){
		@Override
		public void destroy(FileSet toDestroy)
				throws Exception {
//			String physicalBasePath=publisher.getServerPathDir().getAbsolutePath();
//			for(File f:toDestroy.getFiles()){
//				String path=physicalBasePath+f.getStoredUri();
//				try{
//					ServiceUtils.deleteFile(path);
//				}catch(Exception e){
//					logger.warn("Unable to delete "+path,e);
//				}
//			}
		}
	};
	
	private static Generator<Layer> layerDestroyer=new Generator<Layer>(null){
		public void destroy(Layer toDestroy) throws Exception {
			GISUtils.deleteLayer(toDestroy.getLayerInfo());
		};
	};
	
	private static Generator<WMSContext> wmsContextDestroyer=new Generator<WMSContext>(null){
		@Override
		public void destroy(WMSContext toDestroy) throws Exception {
			GISUtils.deleteWMSContext(toDestroy.getWmsContextInfo());
		}
	};
	
	
	
	
	public DeletionMonitor(long interval) {
		super("SUBMITTED_DELETION");
		this.interval=interval;
	}
	
	@Override
	public void run() {
		while(true){
		try{
			
			ArrayList<Field> toDeleteFilter=new ArrayList<Field>();
			toDeleteFilter.add(new Field(SubmittedFields.todelete+"",true+"",FieldType.BOOLEAN));
			List<Submitted> foundList=SubmittedManager.getList(toDeleteFilter);
			if(foundList.size()>0)logger.trace("Found "+foundList.size()+" to deleteObjects");
			for(Submitted toDeleteSubmitted:foundList){
				try{
					long start=System.currentTimeMillis();
					logger.debug("Deleting submitted "+toDeleteSubmitted);
					if(toDeleteSubmitted.getIsAquaMap()){
						boolean deleteLayer=true;
						boolean deleteFileSet=true;
						if(!toDeleteSubmitted.getIsCustomized()){
							ArrayList<Field> fileSetFilter=new ArrayList<Field>();
							fileSetFilter.add(new Field(SubmittedFields.filesetid+"",toDeleteSubmitted.getFileSetId(),FieldType.STRING));
							if(SubmittedManager.getList(fileSetFilter).size()>0) deleteFileSet=false;
								publisher.deleteById(FileSet.class, fileSetDestroyer, toDeleteSubmitted.getFileSetId());
							ArrayList<Field> gisFilter=new ArrayList<Field>();
							gisFilter.add(new Field(SubmittedFields.iscustomized+"",false+"",FieldType.BOOLEAN));
							gisFilter.add(new Field(SubmittedFields.speciescoverage+"",toDeleteSubmitted.getSpeciesCoverage(),FieldType.STRING));
							for(Submitted found:SubmittedManager.getList(gisFilter)){
								if(!found.getSearchId().equals(toDeleteSubmitted.getSearchId())&&found.getGisPublishedId().equals(toDeleteSubmitted.getGisPublishedId())) {
									deleteLayer=false;
									break;
								}
							}
						}
						if(deleteFileSet)
							try{								
								publisher.deleteById(FileSet.class, fileSetDestroyer, toDeleteSubmitted.getFileSetId());
							}catch(Exception e){
								logger.warn("Unable to delete FileSet "+toDeleteSubmitted.getFileSetId(),e);
							}
						if(deleteLayer)
							try{ 
								publisher.deleteById(Layer.class,layerDestroyer,toDeleteSubmitted.getGisPublishedId());
							}catch(Exception e){
								logger.warn("Unable to delete Layer "+toDeleteSubmitted.getGisPublishedId(),e);
							}
					}else {						
						try{
						publisher.deleteById(WMSContext.class,wmsContextDestroyer,toDeleteSubmitted.getGisPublishedId());
						}catch(Exception e){
							logger.warn("Unable to delete WMS "+toDeleteSubmitted.getGisPublishedId(),e);
						}
					}
					try{
						ServiceUtils.deleteFile(toDeleteSubmitted.getSerializedRequest());
					}catch(Exception e){
						logger.warn("Unable to delete File "+toDeleteSubmitted.getSerializedRequest());
					}
					try{
						ServiceUtils.deleteFile(toDeleteSubmitted.getSerializedObject());
					}catch(Exception e){
						logger.warn("Unable to delete File "+toDeleteSubmitted.getSerializedObject());
					}
					SubmittedManager.deleteFromTables(toDeleteSubmitted.getSearchId());					
					logger.debug("Deleted in "+(System.currentTimeMillis()-start));
				}catch(Exception e){
					logger.warn("Unable to delete submitted "+toDeleteSubmitted,e);
				}
			}
		}catch(Exception e){
			logger.warn("UNEXPECTED EXCEPTION",e);
		}
		finally{			
			try{
				Thread.sleep(interval);			
			}catch(InterruptedException e){
				//WAKE UP
			}
		}
		}
	}
	
	
}
