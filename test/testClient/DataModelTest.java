package testClient;

import java.util.Map.Entry;

import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;

public class DataModelTest {

	public static void main(String args[]){
		Job job= AquaMapsServiceTester.createDummyJob();
		Job copied=new Job(job.toStubsVersion());
		System.out.println("customization elements");
		for(String specId:copied.getEnvelopeCustomization().keySet())
			for(Entry<String, Perturbation> entry:copied.getEnvelopeCustomization().get(specId).entrySet())
				System.out.println(specId+" "+entry.getKey()+" "+entry.getValue().getPerturbationValue());
		System.out.println("Done");
	}
	
}
