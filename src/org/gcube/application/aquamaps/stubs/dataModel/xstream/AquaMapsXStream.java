package org.gcube.application.aquamaps.stubs.dataModel.xstream;


import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.BoundingBox;
import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Envelope;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Filter;
import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.Resource;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Submitted;

import com.thoughtworks.xstream.XStream;

public class AquaMapsXStream extends XStream {

	private static AquaMapsXStream instance=null;
	
	public static AquaMapsXStream getInstance(){
		if(instance==null){
			instance =new AquaMapsXStream();
			//***Process Annotations			
			instance.processAnnotations(new Class[]{
					Envelope.class,
					Species.class,
					AquaMapsObject.class,
					Cell.class,
					Job.class,
					Submitted.class,
					Area.class,
					BoundingBox.class,
					Field.class,
					Filter.class,
					Perturbation.class,
					Resource.class,
			});
			//***Register Converters
			instance.registerConverter(new EnvelopeConverter());
		}
		return instance;
	}
	
	
	
}
