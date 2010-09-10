package org.gcube.application.aquamaps.aquamapsservice.impl.engine.model;

import java.util.Date;

public interface SubmittedI {

	public int getId();
	public String getTitle();
	public Date getDate();
//	public StatusI getStatus();
	public int getsourceId(SourcesType source);
	public String getAuthor();
	public boolean isSubmitted();
	
}
