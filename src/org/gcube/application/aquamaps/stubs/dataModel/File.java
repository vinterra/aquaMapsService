package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.Types.FileType;

public class File {

	private FileType type=FileType.XML;
	private String uuri;
	private String name;
	
	public FileType getType() {
		return type;
	}
	public void setType(FileType type) {
		this.type = type;
	}
	public String getUuri() {
		return uuri;
	}
	public void setUuri(String uuri) {
		this.uuri = uuri;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public File (org.gcube.application.aquamaps.stubs.File toLoad){
		super();
		this.setName(toLoad.getName());
		this.setType(FileType.valueOf(toLoad.getType()));
		this.setUuri(toLoad.getUrl());
	}
	
	public static List<File> load(org.gcube.application.aquamaps.stubs.FileArray toLoad){
		List<File> toReturn= new ArrayList<File>();
		if((toLoad!=null)&&(toLoad.getFileList()!=null))
			for(org.gcube.application.aquamaps.stubs.File f: toLoad.getFileList())
				toReturn.add(new File(f));
		return toReturn;
	}
	
	public static org.gcube.application.aquamaps.stubs.FileArray toStubsVersion(List<File> toConvert){
		List<org.gcube.application.aquamaps.stubs.File> list=new ArrayList<org.gcube.application.aquamaps.stubs.File>();
		for(File obj:toConvert)
			list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.stubs.FileArray(list.toArray(new org.gcube.application.aquamaps.stubs.File[list.size()]));
	}
	
	public org.gcube.application.aquamaps.stubs.File toStubsVersion(){
		org.gcube.application.aquamaps.stubs.File toReturn= new org.gcube.application.aquamaps.stubs.File();
		toReturn.setName(this.name);
		toReturn.setType(this.type.toString());
		toReturn.setUrl(this.uuri);
		return toReturn;
	}
}
