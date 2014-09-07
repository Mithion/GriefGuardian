package com.mithion.griefguardian.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import com.mithion.griefguardian.util.PlayerDataUtils;

public class AdminMode extends CommandBase{

	@Override
	public String getCommandName() {
		return "adminmode";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/adminmode";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
		if (PlayerDataUtils.hasMasterACL(player)){
			PlayerDataUtils.setMasterACL(player, false);
			commandSender.addChatMessage(new ChatComponentText("griefguardian.commands.adminmodeoff"));
		}else{
			PlayerDataUtils.setMasterACL(player, true);
			commandSender.addChatMessage(new ChatComponentText("griefguardian.commands.adminmodeon"));
		}
	}

}
