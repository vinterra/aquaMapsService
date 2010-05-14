package org.gcube.application.aquamaps.aquamapsservice.impl.generators;




public class GroupGenerator {

	public String createLayerData(String fileName){
		return null;
	}
	
//	public static void loadCSV(String tableName,DBSession session, String csvFile) throws Exception{
//		CSVReaderProcessor processor= new CSVReaderProcessor();
//		processor.setDelimiter(HSPECCreationCostants.delimiter);
//		processor.setHasHeader(HSPECCreationCostants.hasHeader);
//		final ArrayList<Object[]> data = new ArrayList<Object[]>();
//		logger.trace("Reading from file "+csvFile); 
//		Reader reader= new InputStreamReader(new FileInputStream(csvFile), Charset.defaultCharset());
//		processor.processStream(reader , new CSVLineProcessor(){
//			public boolean continueProcessing() {return true;}
//			public void processDataLine(int arg0, List<String> arg1) {data.add(arg1.toArray(new String[arg1.size()]));}
//			public void processHeaderLine(int arg0, List<String> arg1) {}});
//		logger.trace("Found "+data.size()+" entries");
//		if(data.size()>0){ 
//			for(Object[] record : data){				
//				session.insertOperationSkippingNulls(tableName, record);				
//			}				
//			logger.trace("Inserted data ");
//		}else logger.warn("No records found");  
//	}
}
