package testClient;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;

public class DBConnectionTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		DBSession session=DBSession.getInternalDBSession();
//		String query=(args.length>0)&&(args[0]!=null)?args[0]:"Select * from selectedSpecies";
//		ResultSet rs=session.executeQuery(query);
//		System.out.println(DBUtils.toJSon(rs));	
//		session.close();
		List<Field> filters=new ArrayList<Field>();
		List<Field> updates=new ArrayList<Field>();		
		updates.add(new Field("fone","",FieldType.STRING));
// 		System.out.println(DBSession.formSelectQueryStringFromFields(filters, "theTable", "gigi", "ASC"));
//		System.out.println(DBSession.formSelectQueryStringFromFields(filters, "theTable", null, null));
//		System.out.println(DBSession.formUpdateQuery(updates, filters, "theTable"));
//		filters.add(new Field("gigi","",FieldType.STRING));
//		updates.add(new Field("ssaani","",FieldType.STRING));
//		System.out.println(DBSession.formSelectQueryStringFromFields(filters, "theTable", "gigi", "ASC"));
//		System.out.println(DBSession.formSelectQueryStringFromFields(filters, "theTable", null, null));
//		System.out.println(DBSession.formUpdateQuery(updates, filters, "theTable"));
//		filters.add(new Field("gianni","",FieldType.STRING));
//		System.out.println(DBSession.formSelectQueryStringFromFields(filters, "theTable", "gigi", "ASC"));
//		System.out.println(DBSession.formSelectQueryStringFromFields(filters, "theTable", null, null));
//		System.out.println(DBSession.formUpdateQuery(updates, filters, "theTable"));
//		
		
	}

}
