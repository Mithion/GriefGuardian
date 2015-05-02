package com.mithion.griefguardian.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import com.mithion.griefguardian.claims.ClaimsList;
import com.mithion.griefguardian.claims.ClaimsList.ActionResults;

public class TransferClaim extends CommandBase{

	@Override
	public String getCommandName() {
		return "transferclaim";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/transferclaim <player|team|EVERYONE>";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) throws WrongUsageException {
		if (args.length != 1)
			throw new WrongUsageException(getCommandUsage(commandSender));
		
		ActionResults res;
		try {
			res = ClaimsList.For(commandSender.getEntityWorld()).tryTransferClaim(getCommandSenderAsPlayer(commandSender), args[0]);
		} catch (PlayerNotFoundException e) {
			e.printStackTrace();
			return;
		}
		commandSender.addChatMessage(new ChatComponentText(res.message));
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender commandSender, String[] currentArgs, BlockPos pos) {		
		ArrayList<String> choices = new ArrayList<String>();
		if (currentArgs.length == 1){ //need target
			choices.add("EVERYONE");
			for (Object o : commandSender.getEntityWorld().playerEntities){
				EntityPlayer p = (EntityPlayer)o;
				choices.add(p.getCommandSenderEntity().getName());
			}
			for (Object o : commandSender.getEntityWorld().getScoreboard().getTeamNames()){
				String s = (String)o;
				choices.add(s);
			}
			return getListOfStringsMatchingLastWord(currentArgs, choices.toArray(new String[choices.size()]));
		}
		return choices;
	}

}
