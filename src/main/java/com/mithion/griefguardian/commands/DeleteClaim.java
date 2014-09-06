package com.mithion.griefguardian.commands;

import com.mithion.griefguardian.claims.ClaimsList;
import com.mithion.griefguardian.claims.ClaimsList.ActionResults;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

public class DeleteClaim extends CommandBase{

	@Override
	public String getCommandName() {
		return "deleteclaim";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/deleteclaim";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
		NBTTagCompound compound = player.getEntityData();
		ActionResults res = ClaimsList.For(commandSender.getEntityWorld()).tryDeleteClaim(player);
		commandSender.addChatMessage(new ChatComponentText(res.message));
	}
}
