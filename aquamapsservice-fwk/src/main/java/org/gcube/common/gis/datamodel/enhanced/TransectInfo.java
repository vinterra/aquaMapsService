package org.gcube.common.gis.datamodel.enhanced;

import java.util.ArrayList;

import org.gcube.common.core.types.StringArray;
import org.gcube.common.gis.datamodel.utils.Utils;
import org.gcube_system.namespaces.application.aquamaps.gistypes.TransectInfoType;

public class TransectInfo {
	private boolean enabled;
	private String table;
	private int maxelements;
	private int minimumgap;
	private ArrayList<String> fields;
	
	public TransectInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public TransectInfo(boolean enabled, String table, int maxelements,
			int minimumgap, ArrayList<String> fields) {
		super();
		this.enabled = enabled;
		this.table = table;
		this.maxelements = maxelements;
		this.minimumgap = minimumgap;
		this.fields = fields;
	}

	public TransectInfo(TransectInfoType toLoad){
		this.setFields(Utils.loadString(toLoad.getFields()));
		this.setMaxelements(toLoad.getMaxelements());
		this.setMinimumgap(toLoad.getMinimumgap());
		this.setTable(toLoad.getTable());
		this.setEnabled(toLoad.isEnabled());
	}
	
	public TransectInfoType toStubsVersion() {
		TransectInfoType res = new TransectInfoType();
		res.setEnabled(this.isEnabled());
		res.setFields(new StringArray(this.getFields().toArray(new String[this.getFields().size()])));
		res.setMaxelements(this.getMaxelements());
		res.setMinimumgap(this.getMinimumgap());
		res.setTable(this.getTable());
		return res;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public int getMaxelements() {
		return maxelements;
	}

	public void setMaxelements(int maxelements) {
		this.maxelements = maxelements;
	}

	public int getMinimumgap() {
		return minimumgap;
	}

	public void setMinimumgap(int minimumgap) {
		this.minimumgap = minimumgap;
	}

	public ArrayList<String> getFields() {
		return fields;
	}

	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}
}
