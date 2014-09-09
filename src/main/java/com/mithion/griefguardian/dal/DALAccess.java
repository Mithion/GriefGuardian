package com.mithion.griefguardian.dal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.mithion.griefguardian.util.AdminUtils;

import cpw.mods.fml.common.FMLLog;

public class DALAccess {
	private boolean mysql_loaded = false;

	/**
	 * Checks the database, auto-creates if it doesn't already exist
	 */
	public void checkDatabase(){
		FMLLog.info("GG >> Checking DAL");
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
		FMLLog.info("GG >> DAL is OK");
	}

	private int createIDForUser(EntityPlayerMP player){
		if (!mysql_loaded)
			return -1;

		DBInterface handler = new DBInterface();

		int id = -1;

		try{			
			if (handler.openConnection()){
				handler.prepareTransaction();

				PreparedStatement statement = handler.prepareStatementWithGenID("INSERT INTO users (username, ipaddr) values (?, ?)");
				statement.setString(1, player.getCommandSenderName());
				statement.setString(2, player.getPlayerIP());
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

	private int getIDForUser(EntityPlayerMP player){
		if (!mysql_loaded)
			return -1;

		int id = -1;

		DBInterface handler = new DBInterface();

		try{
			if (handler.openConnection()){
				PreparedStatement statement = handler.prepareStatement("SELECT id FROM users WHERE username = ?");
				statement.setString(1, player.getCommandSenderName());
				handler._rs = statement.executeQuery();

				if (handler._rs.next()){
					id = handler._rs.getInt(1);
				}else{
					id = createIDForUser(player);
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
		if (!mysql_loaded)
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


	private int createIDForWorld(World world){
		if (!mysql_loaded)
			return -1;

		DBInterface handler = new DBInterface();

		int id = -1;

		try{			
			if (handler.openConnection()){
				handler.prepareTransaction();

				PreparedStatement statement = handler.prepareStatementWithGenID("INSERT INTO worlds (dim, `name`) values (?, ?)");
				statement.setInt(1, world.provider.dimensionId);
				statement.setString(2, world.provider.getDimensionName());
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

	private int getIDForWorld(World world){
		if (!mysql_loaded)
			return -1;

		int id = -1;

		DBInterface handler = new DBInterface();

		try{
			if (handler.openConnection()){
				PreparedStatement statement = handler.prepareStatement("SELECT * FROM worlds WHERE dim = ?");
				statement.setInt(1, world.provider.dimensionId);
				handler._rs = statement.executeQuery();

				if (handler._rs.next()){
					id = handler._rs.getInt(1);
				}else{
					id = createIDForWorld(world);
				}				
			}
		}catch(SQLException sqlex){
			sqlex.printStackTrace();
		}finally{
			handler.cleanup();
		}

		return id;
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

		DBInterface handler = new DBInterface();

		try{
			if (handler.openConnection()){
				int player_id = getIDForUser(player);
				int world_id = getIDForWorld(player.getEntityWorld());
				handler.prepareTransaction();
				PreparedStatement statement = handler.prepareStatement("INSERT INTO actions "
						+ "(user, `action`, world, xCoord, yCoord, zCoord, unlocalizedName, metadata, `desc`) "
						+ "VALUES "
						+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)");

				statement.setInt(1, player_id);
				statement.setInt(2, actionID);
				statement.setInt(3, world_id);
				statement.setInt(4, x);
				statement.setInt(5, y);
				statement.setInt(6, z);
				statement.setString(7, stack != null ? stack.getUnlocalizedName() : null);
				statement.setInt(8, stack != null ? stack.getItemDamage() : -1);
				statement.setString(9, String.format(msgFormat, params));

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

	public boolean tempBanUser(String player, long unbanTime){
		return setBanTime(player, unbanTime, AdminUtils.STATUS_TEMPBANNED, false);
	}

	public boolean permaBanUser(String player, long currentTime){
		return setBanTime(player, Long.MAX_VALUE, AdminUtils.STATUS_PERMABANNED, false);
	}

	public boolean tempBanIP(String ipAddr, long unbanTime){
		return setBanTime(ipAddr, unbanTime, AdminUtils.STATUS_TEMPBANNED_IP, true);
	}

	public boolean permaBanIP(String player, long currentTime){
		return setBanTime(player, Long.MAX_VALUE, AdminUtils.STATUS_PERMABANNED_IP, true);
	}
	
	public void unbanUser(String userName, boolean unbanIP){
		setBanTime(userName, 0, AdminUtils.STATUS_ACTIVE, false);
		if (unbanIP)
			setBanTime(getIPForUser(userName), 0, AdminUtils.STATUS_ACTIVE, true);
	}
}
