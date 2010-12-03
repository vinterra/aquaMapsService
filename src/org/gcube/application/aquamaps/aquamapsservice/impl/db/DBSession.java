package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager.DBType;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.common.core.utils.logging.GCUBELog;





/**
 * 
 * @author lucio
 *
 */
public class DBSession {
	
	private static GCUBELog logger= new GCUBELog(DBSession.class);
	

	
	private Connection connection;
	
	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	public static enum ENGINE{MyISAM, InnoDB};
	
	public static enum ALTER_OPERATION{MODIFY, ADD};
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static DBSession openSession(DBType type) throws Exception{
	
		
		Connection conn=PoolManager.getConnection(type);
		return new DBSession(conn);		
		
	}
	
	private DBSession(Connection conn){
		this.connection= conn;
	}
	
	public void close() throws Exception{
		this.connection.close();
	}
	
	public void disableAutoCommit() throws Exception{
		this.connection.setAutoCommit(false);
	}
	
	public void commit() throws Exception{
		this.connection.commit();
	}
	
	/**
	 * 
	 * @param query
	 * @throws Exception
	 */
	public ResultSet executeQuery(String query) throws Exception{
		Statement statement=this.connection.createStatement();
		return statement.executeQuery(query);
	}
			
	public ResultSet executeFilteredQuery(List<Field> filters, String table, String orderColumn, String orderMode)throws Exception{
		StringBuilder queryString=new StringBuilder("Select * from "+table);
		if(filters.size()>0) {
			queryString.append(" where ");
			for(Field f:filters)
				queryString.append(f.getName()+"=? AND ");
			queryString.delete(queryString.lastIndexOf("AND"), queryString.lastIndexOf("AND")+3);
		}	
		if(orderColumn!=null){
			queryString.append(" order by "+orderColumn+" "+orderMode);
		}
		
		PreparedStatement ps=preparedStatement(queryString.toString());
		
		for(int i=0;i<filters.size();i++){
			Field f=filters.get(i);
			switch(f.getType()){
			case BOOLEAN: ps.setBoolean(i+1, Boolean.parseBoolean(f.getValue()));
							break;
			case DOUBLE: ps.setDouble(i+1, Double.parseDouble(f.getValue()));
							break;
			case INTEGER: ps.setInt(i+1, Integer.parseInt(f.getValue()));
			break;				
			case STRING: ps.setString(i+1,f.getValue());
			break;
			
			}			
		}
		return ps.executeQuery();
	}
	
	
	
	public void createTable(String tableName, String[] columnsAndConstraintDefinition, ENGINE ... engine) throws Exception{
		Statement statement = connection.createStatement();
		
		StringBuilder createQuery= new StringBuilder("CREATE TABLE IF NOT EXISTS "+tableName+" (");
		for (String singleColumnDef:columnsAndConstraintDefinition)			
			createQuery.append(singleColumnDef+",");
		
		createQuery.deleteCharAt(createQuery.length()-1);
		createQuery.append(") CHARACTER SET utf8 COLLATE utf8_general_ci ;");
		
		logger.debug("the query is: " + createQuery.toString());
		statement.executeUpdate(createQuery.toString());
		statement.close();
	}
	
	public void disableKeys(String tableName) throws Exception{
		Statement statement = connection.createStatement();
		statement.execute("alter table "+tableName+" DISABLE KEYS");
		statement.close();
	}
	
	public void enableKeys(String tableName) throws Exception{
		Statement statement = connection.createStatement();
		statement.execute("alter table "+tableName+" ENABLE KEYS");
		statement.close();
	}
	
	
	public void createTable(String tableName, String[] columnsAndConstraintDefinition, int numParitions, String partitioningKey, ENGINE ... engines ) throws Exception{
		Statement statement = connection.createStatement();
		
		String engine=engines.length==0?"":"ENGINE="+engines[0].toString();
		StringBuilder createQuery= new StringBuilder("CREATE TABLE IF NOT EXISTS "+tableName+" (");
		for (String singleColumnDef:columnsAndConstraintDefinition)			
			createQuery.append(singleColumnDef+",");
		
		
		createQuery.deleteCharAt(createQuery.length()-1);
		createQuery.append(") "+engine+" CHARACTER SET utf8 COLLATE utf8_general_ci  PARTITION BY KEY("+partitioningKey+") PARTITIONS "+numParitions+" ;");
		
		logger.debug("the query is: " + createQuery.toString());
		statement.executeUpdate(createQuery.toString());
		statement.close();
	}
	
	
	
	public void createLikeTable(String newTableName, String oldTable ) throws Exception{
		Statement statement = connection.createStatement();
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS "+newTableName+" LIKE "+oldTable);
		logger.debug("the like creation is : CREATE TABLE IF NOT EXISTS "+newTableName+" LIKE "+oldTable);
		statement.close();
	}
	
	public void alterColumn(String tableName, ALTER_OPERATION op, String... columnsAndConstraintDefinition) throws Exception{
		Statement statement = connection.createStatement();
		StringBuilder createQuery= new StringBuilder("ALTER TABLE "+tableName+" ");
		for (String singleColumnDef:columnsAndConstraintDefinition)			
			createQuery.append(" "+op.toString()+" COLUMN "+singleColumnDef+",");
		
		createQuery.deleteCharAt(createQuery.length()-1);
		createQuery.append(";");
		
		logger.debug("the query is: " + createQuery.toString());
		try{
			statement.executeUpdate(createQuery.toString());
		}catch(SQLException sqle){logger.warn("error altering table");}
		statement.close();
	}
	
	public void createIndex(String tableName, String columnName) throws Exception{
		Statement statement = connection.createStatement();
		StringBuilder createQuery= new StringBuilder("CREATE INDEX IDX_"+tableName+"_"+columnName+" ON "+tableName+"("+columnName+");");
		logger.debug("the query is: " + createQuery.toString());
		statement.executeUpdate(createQuery.toString());
		statement.close();
	}
	
	public void deleteColumn(String tableName, String columnName) throws Exception{
		Statement statement = connection.createStatement();
		String query="ALTER TABLE "+tableName+" drop column "+columnName;
		logger.debug("the query is: " + query);
		statement.executeUpdate(query.toString());
		statement.close();
	}
	
	/**
	 * 
	 * @param tableName
	 * @param values
	 * @throws Exception
	 */
	public void insertOperation(String tableName, Object... values) throws Exception{
		if (values.length==0) throw new Exception("the number of values passed as argument are 0");
		PreparedStatement ps= getPreparedStatementForInsert(values.length, tableName);
		for (int i =1; i<=values.length; i++){
			if (values[i-1].getClass().isAssignableFrom(String.class))
				ps.setString(i,(String) values[i-1]);
			else
				ps.setObject(i, values[i-1]);
		}
		ps.executeUpdate();
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public PreparedStatement preparedStatement(String query) throws Exception{
		return this.connection.prepareStatement(query);
	}

	public PreparedStatement getPreparedStatementForInsert(int numValues, String table) throws Exception{
		StringBuilder preparedQuery=new StringBuilder("INSERT INTO "+table+" VALUES (");
		for (int i =0; i<numValues; i++)
			preparedQuery.append("?,");
		
		logger.debug(" the values are "+ numValues);
		
		preparedQuery.deleteCharAt(preparedQuery.length()-1);
		preparedQuery.append(");");
		
		logger.trace("the prepared statement is :"+ preparedQuery.toString());
		PreparedStatement ps= connection.prepareStatement(preparedQuery.toString());
		return ps;
	}
	
	public void dropTable(String table) throws Exception{
		Statement statement = connection.createStatement();
		statement.executeUpdate("DROP TABLE "+table+";");
		statement.close();
	}
	
	public int getTableCount(String tableName) throws Exception{
		Statement statement = connection.createStatement();
		ResultSet rs =statement.executeQuery("SELECT COUNT(*) FROM "+tableName);
		rs.next();
		return rs.getInt(1);
	}
	
	
	public List<List<String>> showTableMetadata(String tableName, String... whereClause) throws Exception{
		String query="SHOW COLUMNS FROM "+tableName+" "+((whereClause!=null && whereClause.length>0)?"WHERE "+whereClause[0]:"")+";";
		logger.debug("executing query: "+query);
		ResultSet rs=this.executeQuery(query);
		int columns=rs.getMetaData().getColumnCount();
		List<List<String>> table=  new ArrayList<List<String>>();
		while (rs.next()){
			List<String> row= new ArrayList<String>();
			for (int i=1; i<=columns; i++)
				row.add(rs.getString(i));
			table.add(row);
		}
		return table;
	}
	
	public void executeUpdate(String query) throws Exception{
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
	}
		
}