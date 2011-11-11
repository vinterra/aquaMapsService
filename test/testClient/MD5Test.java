package testClient;

import java.util.Set;
import java.util.TreeSet;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;

public class MD5Test {
	public static void main(String[] args) throws Exception{
		Set<Species> set= new TreeSet<Species>();
		set.add(new Species("Fis-1"));
		set.add(new Species("Fis-10"));
		set.add(new Species("ABS"));
		for(Species s:set)
			System.out.println(s.getId());
		System.out.println(AquaMapsObject.generateMD5(set,""));
		Set<Species> setReplica= new TreeSet<Species>();
		setReplica.add(new Species("Fis-10"));
		setReplica.add(new Species("Fis-1"));
		setReplica.add(new Species("ABS"));
		for(Species s:setReplica)
			System.out.println(s.getId());
		System.out.println(AquaMapsObject.generateMD5(setReplica,""));
		
	}
}
