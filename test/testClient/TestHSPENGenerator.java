package testClient;

import org.gcube.application.aquamaps.aquamapsservice.impl.perturbation.HSPECGenerator;

public class TestHSPENGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		if (args.length!=4)System.out.println(">java TestHSPENGenerator hcaf-table-name hspen-table-name hspec-table-name occurenceCells-table-name");
		HSPECGenerator gen=new HSPECGenerator(args[0], args[1], args[2],args[3]);
		System.out.println("the result table is "+gen.generate());

	}

}
