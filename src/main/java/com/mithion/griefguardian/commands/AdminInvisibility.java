package com.mithion.griefguardian.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import com.mithion.griefguardian.util.PlayerDataUtils;

public class AdminInvisibility extends CommandBase {

	@Override
	public String getCommandName() {
		return "admininvisibility";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/admininvisibility";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
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
		if (PlayerDataUtils.isAdminInvisible(player)){
			PlayerDataUtils.setAdminInvisiblity(player, false);
			commandSender.addChatMessage(new ChatComponentText("griefguardian.commands.admininvisoff"));
		}else{
			PlayerDataUtils.setAdminInvisiblity(player, true);
			commandSender.addChatMessage(new ChatComponentText("griefguardian.commands.admininvison"));
		}
	}
	
}
