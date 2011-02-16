package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HSPECFields;
import org.gcube.common.core.utils.logging.GCUBELog;





/**
 * 
 * @author lucio
 *
 */
public abstract class DBSession {

	protected static GCUBELog logger= new GCUBELog(DBSession.class);



	protected Connection connection;

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


	public static DBSession getInternalDBSession()throws Exception{
		try{
			Connection conn=PoolManager.getInternalDBConnection();
			switch(ServiceContext.getContext().getInternalDBType()){
			case mySql: return new MySQLDBSession(conn);
			default: return new PostGresSQLDBSession(conn);
			}
		}catch(Exception e){
			logger.fatal("ERROR ON OPENING CONNECTION ",e);
			logger.fatal("Connection parameters were : ");
			logger.fatal("USER : "+ServiceContext.getContext().getInternalDbUsername());
			logger.fatal("PASSWORD : "+ServiceContext.getContext().getInternalDbPassword());
			logger.fatal("DB NAME : "+ServiceContext.getContext().getInternalDBName());
			logger.fatal("DB HOST : "+ServiceContext.getContext().getInternalDBHost());
			logger.fatal("DB PORT : "+ServiceContext.getContext().getInternalDBPort());
			logger.fatal("TYPE : "+ServiceContext.getContext().getInternalDBType());
			logger.fatal("Connection String was : "+PoolManager.getInternalConnectionString());
			throw e;
		}
	}

	public static DBSession getPostGisDBSession()throws Exception{
		return new PostGresSQLDBSession(PoolManager.getPostGisDBConnection());
	}


	protected DBSession(Connection conn){
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


	@Deprecated
	protected DBSession(){}


	/**
	 * 
	 * @param query
	 * @throws Exception
	 */
	@Deprecated
	public ResultSet executeQuery(String query) throws Exception{
		Statement statement=this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		return statement.executeQuery(query);
	}

	//************ DATA DEFINITION

	public abstract void createTable(String tableName, String[] columnsAndConstraintDefinition) throws Exception;

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


	public abstract void createLikeTable(String newTableName, String oldTable ) throws Exception;

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


	public void dropTable(String table) throws Exception{
		Statement statement = connection.createStatement();
		statement.executeUpdate("DROP TABLE IF EXISTS "+table+" ");
		statement.close();
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


	//*********************** DATA MANIPULATION

	//*************** PREPARED STATEMENTS

	public abstract PreparedStatement getFilterCellByAreaQuery(HSPECFields filterByCodeType, String sourceTableName, String destinationTableName)throws Exception;

	protected PreparedStatement getPreparedStatementForCount(List<Field> filters, String tableName)throws SQLException{
		return connection.prepareStatement(formSelectCountString(filters, tableName));
	}

	public PreparedStatement getPreparedStatementForQuery(List<Field> filters, String table,String orderColumn,String orderDirection) throws SQLException{
		return connection.prepareStatement(formSelectQueryStringFromFields(filters, table,orderColumn,orderDirection));
	}	

	public PreparedStatement getPreparedStatementForUpdate(List<Field> toSet,List<Field> keys,String tableName)throws SQLException{
		return this.connection.prepareStatement(formUpdateQuery(toSet, keys, tableName),Statement.RETURN_GENERATED_KEYS);
	}

	public PreparedStatement getPreparedStatementForInsertFromSelect(List<Field> fields, String destTable,String srcTable) throws Exception{

		String query="INSERT INTO "+destTable+" ( "+formSelectQueryStringFromFields(fields, srcTable,null,null)+" )";
		logger.trace("the prepared statement is :"+ query);
		PreparedStatement ps= preparedStatement(query);
		return ps;
	}

	public PreparedStatement getPreparedStatementForInsert(List<Field> fields, String table) throws Exception{
		StringBuilder fieldsName=new StringBuilder("(");
		StringBuilder fieldsValues=new StringBuilder("(");
		for (Field f: fields){
			fieldsValues.append("?,");
			fieldsName.append(f.getName()+",");
		}

		logger.debug(" the values are "+ fields.size());

		fieldsValues.deleteCharAt(fieldsValues.length()-1);
		fieldsValues.append(")");
		fieldsName.deleteCharAt(fieldsName.length()-1);
		fieldsName.append(")");

		String query="INSERT INTO "+table+" "+fieldsName+" VALUES "+fieldsValues;
		logger.trace("the prepared statement is :"+ query);
		PreparedStatement ps= connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
		return ps;
	}

	public PreparedStatement getPreparedStatementForDelete(List<Field> fields, String table) throws Exception{
		PreparedStatement ps= preparedStatement(formDeletetQueryStringFromFields(fields, table));
		return ps;
	}

	@Deprecated
	public PreparedStatement preparedStatement(String query) throws Exception{
		return this.connection.prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	}
		
	public abstract PreparedStatement fillParameters(List<Field> fields,
			PreparedStatement ps) throws SQLException ;
	
	
	
	//************ EXECUTED OPERATIONS


	public abstract boolean checkExist(String tableName, List<Field> keys)throws Exception;
	public abstract List<List<Field>> insertOperation(String tableName, List<List<Field>> rows) throws Exception;
	public abstract int updateOperation(String tableName, List<List<Field>> keys,List<List<Field>> rows) throws Exception;
	public abstract ResultSet executeFilteredQuery(List<Field> filters, String table, String orderColumn, String orderMode)throws Exception;


	public abstract int getCount(String tableName, List<Field> filters) throws Exception;
	public abstract int deleteOperation(String tableName, List<Field> filters) throws Exception;


	public int getTableCount(String tableName) throws Exception{
		Statement statement = connection.createStatement();
		ResultSet rs =statement.executeQuery("SELECT COUNT(*) FROM "+tableName);
		rs.next();
		return rs.getInt(1);
	}


	@Deprecated
	public void executeUpdate(String query) throws Exception{
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
	}


	protected List<List<Field>> getGeneratedKeys(PreparedStatement ps) throws SQLException{
		ResultSet rs=ps.getGeneratedKeys();
		ResultSetMetaData rsMeta=rs.getMetaData();
		List<List<Field>> toReturn= new ArrayList<List<Field>>();
		while(rs.next()){
			List<Field> row= new ArrayList<Field>();
			for(int i=1;i<=rsMeta.getColumnCount();i++)
				row.add(new Field(rsMeta.getColumnName(i),rs.getString(i),FieldType.STRING));
			toReturn.add(row);
		}
		return toReturn;
	}


	//********************* STRING FORM UTILITIES


	protected static String formSelectQueryStringFromFields(List<Field> filters,String table,String sortColumn,String sortDirection){
		String toReturn="SELECT * FROM "+table+
		(((filters!=null)&&filters.size()>0)?" WHERE "+getCondition(filters,"AND"):"")+
		((sortColumn!=null)?" ORDER BY "+sortColumn+" "+sortDirection:"");
		logger.debug("QUERY STRING IS : "+toReturn);
		return toReturn;
	}

	protected static String formSelectCountString(List<Field> filters, String tableName){
		return "SELECT COUNT(*) FROM "+tableName+(((filters!=null)&&filters.size()>0)?" WHERE "+getCondition(filters,"AND"):"");
	}

	protected static String formDeletetQueryStringFromFields(List<Field> filters,String table){
		return "DELETE * FROM "+table+(((filters!=null)&&filters.size()>0)?" WHERE "+getCondition(filters,"AND"):"");
	}

	protected static String formUpdateQuery(List<Field> toSet, List<Field> keys,String tableName){
		String toReturn="UPDATE "+tableName+" SET "+getCondition(toSet,",")+
		(((keys!=null)&&keys.size()>0)?" WHERE "+getCondition(keys,"AND"):"");
		logger.debug("QUERY STRING IS : "+toReturn);
		return toReturn;
	}
	
	private static String getCondition(List<Field> filters,String operator){
		StringBuilder query=new StringBuilder();
		if((filters!=null)&&filters.size()>0){
			for(Field f:filters)query.append(" "+f.getName()+" = ? "+operator);
			query.delete(query.lastIndexOf(operator),query.lastIndexOf(operator)+operator.length());
		}
//		logger.debug("Formed condition string "+query);
		return query+"";
	}

	
}