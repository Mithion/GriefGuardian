package com.mithion.griefguardian.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.config.ConfigKeys;

/*
 * MySQL Data Access Layer class
 * Handles all interactions between MySQL and java
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
class DBInterface {
	private Connection _connection;
	public PreparedStatement _prepStatement;
	public ResultSet _rs;	
	
	/**
	 * Opens the connection to the database
	 * @return True if the connection was opened, otherwise false
	 */
	public boolean openConnection(){		
		String url = String.format("jdbc:mysql://%s:%d/%s", 
				GriefGuardian.instance.config.getString(ConfigKeys.mysql_db_host),
				GriefGuardian.instance.config.getInt(ConfigKeys.mysql_db_port),
				GriefGuardian.instance.config.getString(ConfigKeys.mysql_db_name));
		String user = GriefGuardian.instance.config.getString(ConfigKeys.mysql_db_user);
		String password = GriefGuardian.instance.config.getString(ConfigKeys.mysql_db_pass);

		try{
			if (_connection != null && !_connection.isClosed()){
				return true;
			}
			
			_connection = DriverManager.getConnection(url, user, password);
			return true;
		}catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Cleans up all connections, result sets, and statements
	 */
	void cleanup(){
		try {
			if (_rs != null) {
				_rs.close();
			}
			if (_prepStatement != null) {
				_prepStatement.close();
			}
			if (_connection != null) {
				_connection.close();
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void reset(){
		try{
			if (_rs != null) {
				_rs.close();
				_rs = null;
			}
			if (_prepStatement != null) {
				_prepStatement.close();
				_prepStatement = null;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void prepareTransaction(){
		try {
			_connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void rollbackTransaction(){
		try {
			_connection.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void commitChanges(){
		try {
			_connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public PreparedStatement prepareStatement(String syntax){
		try {
			_prepStatement = _connection.prepareStatement(syntax);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return _prepStatement;
	}
	
	public PreparedStatement prepareStatementWithGenID(String syntax){
		try {
			_prepStatement = _connection.prepareStatement(syntax, Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return _prepStatement;
	}
}
