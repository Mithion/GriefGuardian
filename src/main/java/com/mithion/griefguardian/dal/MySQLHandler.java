package com.mithion.griefguardian.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.config.ConfigKeys;

import cpw.mods.fml.common.FMLLog;

/*
 * MySQL Data Access Layer class
 * Handles all interactions between MySQL and this mod
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
public class MySQLHandler {
	private Connection _connection;
	private Statement _statement;
	private PreparedStatement _prepStatement;
	private ResultSet _rs;	

	/**
	 * Opens the connection to the database
	 * @return True if the connection was opened, otherwise false
	 */
	private boolean openConnection(){
		String url = String.format("jdbc:mysql://%s:%d/%s", 
				GriefGuardian.instance.config.getString(ConfigKeys.mysql_db_host),
				GriefGuardian.instance.config.getInt(ConfigKeys.mysql_db_port),
				GriefGuardian.instance.config.getString(ConfigKeys.mysql_db_name));
		String user = GriefGuardian.instance.config.getString(ConfigKeys.mysql_db_user);
		String password = GriefGuardian.instance.config.getString(ConfigKeys.mysql_db_pass);

		try{
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
	private void cleanup(){
		try {
			if (_rs != null) {
				_rs.close();
			}
			if (_statement != null) {
				_statement.close();
			}
			if (_connection != null) {
				_connection.close();
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Performs a query against the database.  Should be used *very* sparingly, as the SQL scrubbing isn't ideal.
	 * Better to perform prepared statements against the database with known parameters.
	 * @param syntax The SQL code to run
	 */
	public void query(String syntax){
		syntax = scrub(syntax);
		try{
			if (openConnection()){
				_statement = _connection.createStatement();
				_rs = _statement.executeQuery(syntax);
			}
			//TODO:  Do something with the results
		}catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			cleanup();
		}
	}

	/**
	 * Really lazy SQL query scrubber - replaces invalid characters.  Prepared statements should be used wherever possible though.
	 * @param syntax the syntax to clean
	 */
	private String scrub(String syntax){  
		return syntax.replaceAll("([^A-Za-z0-9.,'\"% _-]+)", "");  
	}

	/**
	 * Gets all registered loggable actions in the database and creates a local construct for them
	 */
	public HashMap<Integer, LoggableAction> getLoggableActions(){
		HashMap<Integer, LoggableAction> actions = new HashMap<Integer, LoggableAction>();
		try{
			if (openConnection()){
				_prepStatement = _connection.prepareStatement("SELECT * FROM actiontypes");
				_rs = _prepStatement.executeQuery();

				while (_rs.next()){
					LoggableAction action = new LoggableAction(_rs.getInt("id"), _rs.getString("desc"));
					actions.put(action.getID(), action);
				}
			}
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			cleanup();
		}
		return actions;
	}

	/**
	 * Checks the database, auto-creates if it doesn't already exist
	 */
	public void checkDatabase(){
		FMLLog.info("GG >> Checking DAL");
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}catch(Throwable t){
			FMLLog.warning("GG >> You don't have the MySQL JDBC driver installed...you're going to need that!  DAL check failed!");
			return;
		}
		if (!openConnection()){
			FMLLog.warning("GG >> DAL couldn't connect to MySQL!");
			return;
		}else{
			cleanup();
		}
		FMLLog.info("GG >> DAL is OK");
	}
}
