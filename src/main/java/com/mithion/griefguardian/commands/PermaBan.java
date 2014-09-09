package com.mithion.griefguardian.commands;

import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.util.AdminUtils;

public class PermaBan extends CommandBase{

	@Override
	public String getCommandName() {
		return "permaban";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/permaban <username> [should_ban_ip] [no_match_required]";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args){
		if (args.length != 1 && args.length != 2 && args.length != 3){
			throw new WrongUsageException(getCommandUsage(commandSender));
		}
		
		String identString = args[0];
		boolean ipBan = false;
		boolean noMatch = false;
		try{
			if (args.length >= 2)
				ipBan = parseBoolean(commandSender, args[1]);
		}catch (Throwable t){
			throw new WrongUsageException("Arg 3 must be a boolean (true or false)!");
		}
		try{
			if (args.length == 3)
				noMatch = parseBoolean(commandSender, args[2]);
		}catch (Throwable t){
			throw new WrongUsageException("Arg 4 must be a boolean (true or false)!");
		}
		
		long time = MinecraftServer.getSystemTimeMillis();
		
		EntityPlayerMP player = getPlayer(commandSender, identString);
		
		if (player == null && !noMatch){
			throw new WrongUsageException("Player not found.  Consider setting the no match flag if the player is not online.");
		}
		
		if (ipBan){
			if (player == null && !Pattern.matches(AdminUtils.IPADDRESS_PATTERN, identString)){
				throw new WrongUsageException("Invalid IP Address specified.");
			}
			GriefGuardian._dal.permaBanIP(identString, time);
			List<EntityPlayerMP> list = MinecraftServer.getServer().getConfigurationManager().getPlayerList(identString);
			for (EntityPlayerMP p : list){
				p.playerNetServerHandler.kickPlayerFromServer(String.format("You have been permanently banned from the server."));
			}
		}else{
			GriefGuardian._dal.permaBanUser(identString, time);
			if (player != null){
				player.playerNetServerHandler.kickPlayerFromServer("You have been permanently banned from the server.");
			}
		}
		
		func_152373_a(commandSender, this, "%s has been given a perma ban.", identString);
	}
	
}
