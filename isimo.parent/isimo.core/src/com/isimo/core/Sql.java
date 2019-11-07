package com.isimo.core;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.dom4j.Element;
import org.dom4j.Node;

import com.isimo.core.annotations.IsimoAction;
import com.isimo.core.xml.LocationAwareElement;

@IsimoAction
public class Sql extends AtomicAction {
	public Connection conn = null;

	public Sql(LocationAwareElement pDefinition, Action pParent) {
		super(pDefinition, pParent);
	}
	
	public void connect() throws Exception {
		Driver driver = null;
		DriverManager.registerDriver(driver = (Driver) Class.forName(getDefinition().attributeValue("jdbcdriver")).newInstance());
		conn = DriverManager.getConnection(getDefinition().attributeValue("jdbcurl"), getDefinition().attributeValue("jdbcuser"), getDefinition().attributeValue("jdbcpassword"));		

	}
	
	@Override
	public void executeAtomic() throws Exception {
		connect();
		for(Node stmt: getDefinition().elements("statement")) {
			executeStatement((Element) stmt);
		}
		conn.close();
	}
	
	public void executeStatement(String sql) throws Exception {
		Statement stmt = conn.createStatement();		
		log("Executing sql: "+sql);
		stmt.execute(sql);
		ResultSet rs = stmt.getResultSet();
		if(rs==null) {
			stmt.close();
			return;
		}
		ResultSetMetaData metadata = rs.getMetaData();
		int columncount = metadata.getColumnCount();
		int row = 0;
		while(rs.next()) {
			for(int i = 0; i < columncount;i++) {
				String property = "sqlselect_row"+row+"_col"+i;
				String value = rs.getObject(i+1).toString();
				log("Saving property '"+property+"' with value '"+value+"'");
				testExecutionManager.properties.setProperty(property, value);
			}
			row++;
		}
		rs.close();stmt.close();
	}

	public void executeStatement(Element stmtelem) throws Exception {
		String sql = stmtelem.getTextTrim();
		executeStatement(sql);
	}
}
