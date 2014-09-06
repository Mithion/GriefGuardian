package com.mithion.griefguardian.commands;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.claims.ClaimsList;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class ShowClaims extends CommandBase{

	@Override
	public String getCommandName() {
		return "showclaims";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/showclaims";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
		GriefGuardian.instance.networkWrapper.sendTo(ClaimsList.For(commandSender.getEntityWorld()).createSyncMessage(player), player);
		
		commandSender.addChatMessage(new ChatComponentText("griefguardian.commands.syncing"));
	}

}
