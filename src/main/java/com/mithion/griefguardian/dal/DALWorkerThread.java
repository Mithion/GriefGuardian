package com.mithion.griefguardian.dal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.mithion.griefguardian.util.AdminUtils;

public class DALWorkerThread implements Runnable {

	private ConcurrentLinkedQueue<LogEntry> logQueue;
	private boolean run = true;	
	private boolean log = false;
	private boolean query = false;
	
	@Override
	public void run() {
		logQueue = new ConcurrentLinkedQueue<LogEntry>();
		while (run){
			if (log && logQueue.size() > 0){
				LogEntry l = logQueue.poll();
				createLogEntry(l);
			}
			if (query){
				
			}
		}
	}
	
	public void setLog(){
		log = true;
	}
	
	public void setQuery(){
		query = true;
	}
	
	public synchronized void addLogEntryToQueue(String userName, String ipAddr, int action, int dimID, String dimName, int x, int y, int z, ItemStack stack, String desc, Object...params){
		logQueue.add(new LogEntry(userName, ipAddr, action, dimID, dimName, x, y, z, stack, desc, params));
	}
	
	public synchronized void shutdown(){
		run = false;
	}
	
	private void createLogEntry(LogEntry l){
		DBInterface handler = new DBInterface();

		try{
			if (handler.openConnection()){
				int player_id = getIDForUser(l.userName, l.ipAddr);
				int world_id = getIDForWorld(l.dimension, l.dimensionName);
				handler.prepareTransaction();
				PreparedStatement statement = handler.prepareStatement("INSERT INTO actions "
						+ "(user, `action`, world, xCoord, yCoord, zCoord, unlocalizedName, metadata, `desc`) "
						+ "VALUES "
						+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)");

				statement.setInt(1, player_id);
				statement.setInt(2, l.action);
				statement.setInt(3, world_id);
				statement.setInt(4, l.x);
				statement.setInt(5, l.y);
				statement.setInt(6, l.z);
				statement.setString(7, l.itemIdentifier);
				statement.setInt(8, l.itemMeta);
				statement.setString(9, l.description);

				if (statement.executeUpdate() != 1){
					handler.rollbackTransaction();
				}else{
					handler.commitChanges();
				}
			}
		}catch(SQLException sqlex){
			sqlex.printStackTrace();
		}finally{
			handler.cleanup();
		}
	}

	private int createIDForUser(String player, String ip){
		if (!DALAccess.isMySQLLoaded())
			return -1;

		DBInterface handler = new DBInterface();

		int id = -1;

		try{			
			if (handler.openConnection()){
				handler.prepareTransaction();

				PreparedStatement statement = handler.prepareStatementWithGenID("INSERT INTO users (username, ipaddr) values (?, ?)");
				statement.setString(1, player);
				statement.setString(2, ip);
				if (statement.executeUpdate() == 1){
					handler._rs = statement.getGeneratedKeys();
					if (handler._rs.next())
						id = handler._rs.getInt(1);
				}else{
					handler.rollbackTransaction();
				}
				handler.commitChanges();
			}
		}catch(SQLException sqlex){
			sqlex.printStackTrace();
		}finally{
			handler.cleanup();
		}

		return id;
	}

	private int getIDForUser(String player, String ip){
		if (!DALAccess.isMySQLLoaded())
			return -1;

		int id = -1;

		DBInterface handler = new DBInterface();

		try{
			if (handler.openConnection()){
				PreparedStatement statement = handler.prepareStatement("SELECT id FROM users WHERE username = ?");
				statement.setString(1, player);
				handler._rs = statement.executeQuery();

				if (handler._rs.next()){
					id = handler._rs.getInt(1);
				}else{
					id = createIDForUser(player, ip);
				}				
			}
		}catch(SQLException sqlex){
			sqlex.printStackTrace();
		}finally{
			handler.cleanup();
		}

		return id;
	}
	
	private String getIPForUser(String userName){
		if (!DALAccess.isMySQLLoaded())
			return "";

		String ip = "";

		DBInterface handler = new DBInterface();

		try{
			if (handler.openConnection()){
				PreparedStatement statement = handler.prepareStatement("SELECT ipaddr FROM users WHERE username = ?");
				statement.setString(1, userName);
				handler._rs = statement.executeQuery();

				if (handler._rs.next()){
					ip = handler._rs.getString(1);
				}				
			}
		}catch(SQLException sqlex){
			sqlex.printStackTrace();
		}finally{
			handler.cleanup();
		}

		return ip;
	}

	private int createIDForWorld(int dimensionID, String dimName){
		if (!DALAccess.isMySQLLoaded())
			return -1;

		DBInterface handler = new DBInterface();

		int id = -1;

		try{			
			if (handler.openConnection()){
				handler.prepareTransaction();

				PreparedStatement statement = handler.prepareStatementWithGenID("INSERT INTO worlds (dim, `name`) values (?, ?)");
				statement.setInt(1, dimensionID);
				statement.setString(2, dimName);
				if (statement.executeUpdate() == 1){
					handler._rs = statement.getGeneratedKeys();
					if (handler._rs.next())
						id = handler._rs.getInt(1);
				}else{
					handler.rollbackTransaction();
				}
				handler.commitChanges();
			}
		}catch(SQLException sqlex){
			sqlex.printStackTrace();
		}finally{
			handler.cleanup();
		}

		return id;
	}

	private int getIDForWorld(int dimensionID, String dimName){
		if (!DALAccess.isMySQLLoaded())
			return -1;

		int id = -1;

		DBInterface handler = new DBInterface();

		try{
			if (handler.openConnection()){
				PreparedStatement statement = handler.prepareStatement("SELECT * FROM worlds WHERE dim = ?");
				statement.setInt(1, dimensionID);
				handler._rs = statement.executeQuery();

				if (handler._rs.next()){
					id = handler._rs.getInt(1);
				}else{
					id = createIDForWorld(dimensionID, dimName);
				}				
			}
		}catch(SQLException sqlex){
			sqlex.printStackTrace();
		}finally{
			handler.cleanup();
		}

		return id;
	}

	public synchronized long getUnbanTime(EntityPlayerMP player){
		if (!DALAccess.isMySQLLoaded())
			return 0;

		DBInterface handler = new DBInterface();

		long bantime = 0;

		try{
			if (handler.openConnection()){
				PreparedStatement statement = handler.prepareStatement("SELECT bantime FROM users WHERE username = ? OR ipaddr = ?");

				statement.setString(1, player.getCommandSenderEntity().getName());
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

	private boolean setBanTime(String identifier, long unbanTime, int statusCode, boolean ipaddr){
		DBInterface handler = new DBInterface();

		boolean success = true;

		try{			
			if (handler.openConnection()){
				handler.prepareTransaction();

				PreparedStatement statement;
				if (!ipaddr)
					statement = handler.prepareStatementWithGenID("UPDATE users SET bantime = ?, `status` = ? WHERE username = ?");
				else
					statement = handler.prepareStatementWithGenID("UPDATE users SET bantime = ?, `status` = ? WHERE ipaddr = ?");
				statement.setLong(1, unbanTime);
				statement.setInt(2, statusCode);
				statement.setString(3, identifier);
				int rows = statement.executeUpdate();
				if ((!ipaddr && rows != 1)||(ipaddr && rows == 0)){				
					handler.rollbackTransaction();
					success = false;
				}else{
					handler.commitChanges();
				}
			}
		}catch(SQLException sqlex){
			sqlex.printStackTrace();
		}finally{
			handler.cleanup();
		}

		return success;
	}

	public synchronized boolean tempBanUser(String player, long unbanTime){
		return setBanTime(player, unbanTime, AdminUtils.STATUS_TEMPBANNED, false);
	}

	public synchronized boolean permaBanUser(String player, long currentTime){
		return setBanTime(player, Long.MAX_VALUE, AdminUtils.STATUS_PERMABANNED, false);
	}

	public synchronized boolean tempBanIP(String ipAddr, long unbanTime){
		return setBanTime(ipAddr, unbanTime, AdminUtils.STATUS_TEMPBANNED_IP, true);
	}

	public synchronized boolean permaBanIP(String player, long currentTime){
		return setBanTime(player, Long.MAX_VALUE, AdminUtils.STATUS_PERMABANNED_IP, true);
	}
	
	public synchronized void unbanUser(String userName, boolean unbanIP){
		setBanTime(userName, 0, AdminUtils.STATUS_ACTIVE, false);
		if (unbanIP)
			setBanTime(getIPForUser(userName), 0, AdminUtils.STATUS_ACTIVE, true);
	}
}
