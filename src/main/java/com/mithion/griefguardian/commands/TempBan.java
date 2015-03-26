package com.mithion.griefguardian.commands;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.util.AdminUtils;

public class TempBan extends CommandBase{
	
	@Override
	public String getCommandName() {
		return "tempban";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/tempban <username> <0d0h0m> [should_ban_ip] [no_match_required]";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) throws WrongUsageException{
		if (args.length != 2 && args.length != 3 && args.length != 4){
			throw new WrongUsageException(getCommandUsage(commandSender));
		}
		
		String identString = args[0];
		int days, hours, minutes;
		boolean ipBan = false;
		boolean noMatch = false;
		try{
			Pattern pattern = Pattern.compile("[0-9]+");
			Matcher matcher = pattern.matcher(args[1]);
			matcher.find();
			days = parseInt(matcher.group());
			matcher.find();
			hours = parseInt(matcher.group());
			matcher.find();
			minutes = parseInt(matcher.group());
		}catch (Throwable t){
			throw new WrongUsageException("Arg 2 must be in the format '_d_h_m', where each underscore is replaced by a number.  It represents days, hours, and minutes.");
		}
		try{
			if (args.length >= 3)
				ipBan = parseBoolean(args[2]);
		}catch (Throwable t){
			throw new WrongUsageException("Arg 3 must be a boolean (true or false)!");
		}
		try{
			if (args.length == 4)
				noMatch = parseBoolean(args[3]);
		}catch (Throwable t){
			throw new WrongUsageException("Arg 4 must be a boolean (true or false)!");
		}
		
		long time = MinecraftServer.getCurrentTimeMillis();
		
		EntityPlayerMP player = null;
		try{
			player = getPlayer(commandSender, identString);
		}catch(Throwable t){
			//whatever, we don't care
		}
		
		if (player == null && !noMatch){
			throw new WrongUsageException("Player not found.  Consider setting the no match flag if the player is not online, or if you want to manually specify an IP address.");
		}
		
		Date date = new Date(time);
		date.setDate(date.getDate() + days);
		date.setHours(date.getHours() + hours);
		date.setMinutes(date.getMinutes() + minutes);
		long unbanTime = date.getTime();
		
		if (ipBan){
			if (player == null && !Pattern.matches(AdminUtils.IPADDRESS_PATTERN, identString)){
				throw new WrongUsageException("Invalid IP Address specified.");
			}
			if (player != null){
				identString = player.getPlayerIP();
			}
			GriefGuardian._dal.tempBanIP(identString, unbanTime);
			List<EntityPlayerMP> list = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for (EntityPlayerMP p : list){
				p.playerNetServerHandler.kickPlayerFromServer(String.format("You have been given a temp ban.  The ban will be lifted at %s.", new Date(unbanTime).toGMTString()));
			}
			if (player != null){
				player.playerNetServerHandler.kickPlayerFromServer(String.format("You have been given a temp ban.  The ban will be lifted at %s.", new Date(unbanTime).toGMTString()));
			}
		}else{
			GriefGuardian._dal.tempBanUser(identString, unbanTime);
			if (player != null){
				player.playerNetServerHandler.kickPlayerFromServer(String.format("You have been given a temp ban.  The ban will be lifted at %s.", new Date(unbanTime).toGMTString()));
			}
		}		
		
		//TODO: zet in chat %s has been given a temp ban.
	}
	
}
