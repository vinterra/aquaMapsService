package org.gcube.common.gis.datamodel.utils;

import java.util.ArrayList;

import org.gcube.common.core.types.StringArray;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube_system.namespaces.application.aquamaps.gistypes.LayerArray;
import org.gcube_system.namespaces.application.aquamaps.gistypes.LayerInfoType;



public class Utils {
	public static ArrayList<String> loadString(StringArray sources) {
		try {
			ArrayList<String> res = new ArrayList<String>();
			for (String style: sources.getItems()){
				res.add(style);
			}
			return res;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public static ArrayList<LayerInfo> loadArray(LayerArray sources) {


		ArrayList<LayerInfo> res = new ArrayList<LayerInfo>();
		
		if((sources!=null)&&(sources.getName()!=null))
		for (LayerInfoType layer: sources.getName())
			res.add(new LayerInfo(layer));

		return res;
	}

	public static LayerArray loadArray(ArrayList<LayerInfo> sources) {
		LayerInfoType[] res = new LayerInfoType[(sources!=null)?sources.size():0];
		int i = 0;
		for (LayerInfo layer: sources){
			res[i] = layer.toStubsVersion();
			i += 1;
		}
		return new LayerArray(res);
	}
	
//	public static ArrayList<LayerTypeId> loadLayerTypeIdArray(LayerTypeIdArray sources) {
//
//		ArrayList<LayerTypeId> res = new ArrayList<LayerTypeId>();
//		
//		if((sources!=null)&&(sources.getName()!=null))
//			for ( Layer_type_id layer: sources.getName())
//				res.add(new LayerTypeId(layer));
//	
//			return res;
//	}
//
//	public static LayerTypeIdArray loadLayerTypeIdArray(ArrayList<LayerTypeId> sources) {
//		Layer_type_id[] res = new Layer_type_id[(sources!=null)?sources.size():0];
//		int i = 0;
//		for (LayerTypeId layer: sources){
//			res[i] = layer.toStubsVersion();
//			i += 1;
//		}
//		return new LayerTypeIdArray(res);
//	}
}