package com.mithion.griefguardian.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.claims.ClaimsList;
import com.mithion.griefguardian.util.PlayerDataUtils;

public class HideClaims extends CommandBase{

	@Override
	public String getCommandName() {
		return "hideclaims";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/hideclaims";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		EntityPlayerMP player;
		try {
			player = getCommandSenderAsPlayer(commandSender);
		} catch (PlayerNotFoundException e) {
			e.printStackTrace();
			return;
		}
		GriefGuardian.networkWrapper.sendTo(ClaimsList.For(commandSender.getEntityWorld()).createSyncMessage(player, true), player);
		
		PlayerDataUtils.setRenderClaimsData(player, false);
		commandSender.addChatMessage(new ChatComponentText("griefguardian.commands.hideclaims"));
	}

}
