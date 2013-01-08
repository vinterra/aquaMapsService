package org.gcube.application.aquamaps.aquamapsservice.tests;

import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.junit.runner.RunWith;

//@RunWith(MyContainerTestRunner.class)
public class Test {

	public static Gar SERVICE_GAR() {
		return new Gar("aquamapsservice").addInterfaces("../wsdl")
				.addConfigurations("../config");
	}
	
//	@org.junit.Test
	public void getGar (){
		SERVICE_GAR();
	}
	
}
