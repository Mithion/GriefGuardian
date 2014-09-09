package com.mithion.griefguardian.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import com.mithion.griefguardian.GriefGuardian;

public class UnBan extends CommandBase{

	@Override
	public String getCommandName() {
		return "unban";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/unban <username> [should_unban_ip]";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args){
		if (args.length != 1 && args.length != 2){
			throw new WrongUsageException(getCommandUsage(commandSender));
		}
		
		String identString = args[0];
		boolean clearIpBan = false;
		try{
			if (args.length == 2)
				clearIpBan = parseBoolean(commandSender, args[1]);
		}catch (Throwable t){
			throw new WrongUsageException("Arg 2 must be a boolean (true or false)!");
		}
		
		GriefGuardian._dal.unbanUser(identString, clearIpBan);
		
		func_152373_a(commandSender, this, "%s has been unbanned.", identString);
	}
	
}

