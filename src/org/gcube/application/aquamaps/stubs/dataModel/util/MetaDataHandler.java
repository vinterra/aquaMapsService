package org.gcube.application.aquamaps.stubs.dataModel.util;

import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.BoundingBox;
import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.File;
import org.gcube.application.aquamaps.stubs.dataModel.Filter;
import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.Resource;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Submitted;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class MetaDataHandler {

	private static XStream xStream;


	private static MetaDataHandler instance=null;

	public static MetaDataHandler get(){
		if(instance==null) {
			instance=new MetaDataHandler();
			xStream = new XStream(new DomDriver());
			System.out.println("Processing annotations...");
			// process Annotations and register Converters
			xStream.processAnnotations(new Class[]{
					Species.class,
					Area.class,
					BoundingBox.class,
					Cell.class,
					Field.class,
					File.class,
					Filter.class,
					Perturbation.class,
					Resource.class,
					Submitted.class,
					AquaMapsObject.class,
					Job.class
			});
		}
		return instance;
	}

	public String toXML(Object obj){
		return xStream.toXML(obj);
	}
	public String toJSON(Object obj)throws Exception{
		//TODO implement
		throw new Exception("Method not yet implemented");
	}

	public Object fromXML(String toParse){
		return (Object) xStream.fromXML(toParse);
	}

	public Object fromJSON(String toParse) throws Exception{
		//TODO parse
		throw new Exception("Method not yet implemented");
	}
}
