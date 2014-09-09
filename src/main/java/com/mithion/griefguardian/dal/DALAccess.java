package com.mithion.griefguardian.dal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLLog;

public class DALAccess {
	
	private DALWorkerThread logThread;
	private DALWorkerThread queryThread;
	private static boolean mysql_loaded = false;

	public static synchronized boolean isMySQLLoaded(){
		return mysql_loaded;
	}
	
	/**
	 * Checks the database, auto-creates if it doesn't already exist
	 */
	public void checkDatabase(){
		FMLLog.info("GG >> Checking DAL");		
		logThread = new DALWorkerThread();
		queryThread = new DALWorkerThread();
		logThread.setLog();
		queryThread.setQuery();
		
		try{
			//force the driver class to load
			Class.forName("com.mysql.jdbc.Driver");
		}catch(Throwable t){
			FMLLog.warning("GG >> You don't have the MySQL JDBC driver installed...you're going to need that!  DAL check failed!");
			return;
		}
		DBInterface handler = new DBInterface();
		if (!handler.openConnection()){
			FMLLog.warning("GG >> DAL couldn't connect to MySQL!");
			return;
		}else{
			handler.cleanup();
		}
		mysql_loaded = true;		
		
		FMLLog.info("GG >> DAL is OK.  Starting worker threads...");		
		
		new Thread(logThread, "GG DAL Log Thread").start();
		new Thread(queryThread, "GG DAL Query Thread").start();
		FMLLog.info("GG >> Worker threads running");
	}
	
	public void stopAllThreads(){
		FMLLog.info("DD >> Stopping all worker threads");
		logThread.shutdown();
		queryThread.shutdown();
	}

	/**
	 * Gets all registered loggable actions in the database and creates a local construct for them
	 */
	public HashMap<Integer, LoggableAction> getLoggableActions(){
		HashMap<Integer, LoggableAction> actions = new HashMap<Integer, LoggableAction>();
		if (!mysql_loaded)
			return actions;

		DBInterface handler = new DBInterface();

		try{
			if (handler.openConnection()){
				PreparedStatement statement = handler.prepareStatement("SELECT * FROM actiontypes");
				handler._rs = statement.executeQuery();

				while (handler._rs.next()){
					LoggableAction action = new LoggableAction(handler._rs.getInt("id"), handler._rs.getString("desc"));
					actions.put(action.getID(), action);
				}
			}
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			handler.cleanup();
		}
		return actions;
	}

	public void logAction(EntityPlayerMP player, int actionID, int x, int y, int z, ItemStack stack, String msgFormat, Object ...params){
		if (!mysql_loaded)
			return;
		
		logThread.addLogEntryToQueue(player.getCommandSenderName(), 
				player.getPlayerIP(), 
				actionID, 
				player.worldObj.provider.dimensionId, 
				player.worldObj.provider.getDimensionName(), 
				x, 
				y, 
				z, 
				stack, 
				msgFormat, 
				params);
	}

	public long getUnbanTime(EntityPlayerMP player){
		if (!mysql_loaded)
			return 0;

		DBInterface handler = new DBInterface();

		long bantime = 0;

		try{
			if (handler.openConnection()){
				PreparedStatement statement = handler.prepareStatement("SELECT bantime FROM users WHERE username = ? OR ipaddr = ?");

				statement.setString(1, player.getCommandSenderName());
				statement.setString(2, player.getPlayerIP());

				handler._rs = statement.executeQuery();
				while (handler._rs.next())
					bantime = Math.max(handler._rs.getLong(1), bantime);
			}
		}catch(SQLException sqlex){
			sqlex.printStackTrace();
		}finally{
			handler.cleanup();
		}

		return bantime;
	}

	public void tempBanUser(String player, long unbanTime){
		logThread.tempBanUser(player, unbanTime);
	}

	public void permaBanUser(String player, long currentTime){
		logThread.permaBanUser(player, currentTime);
	}

	public void tempBanIP(String ipAddr, long unbanTime){
		logThread.tempBanIP(ipAddr, unbanTime);
	}

	public void permaBanIP(String player, long currentTime){
		logThread.permaBanIP(player, currentTime);
	}
	
	public void unbanUser(String userName, boolean unbanIP){
		logThread.unbanUser(userName, unbanIP);
	}
}
