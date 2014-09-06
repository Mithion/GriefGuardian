package com.mithion.griefguardian.commands;

import com.mithion.griefguardian.claims.ClaimsList;
import com.mithion.griefguardian.claims.ClaimsList.ActionResults;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

public class ClaimCommand extends CommandBase{

	public static final String CLAIM_STATE = "gg_claim_state";
	public static final String CLAIM_START_X = "gg_claim_start_x";
	public static final String CLAIM_START_Y = "gg_claim_start_y";
	public static final String CLAIM_START_Z = "gg_claim_start_z";
	
	@Override
	public String getCommandName() {
		return "claim";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/claim";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
		NBTTagCompound compound = player.getEntityData();
		if (!compound.hasKey(CLAIM_STATE) || compound.getBoolean(CLAIM_STATE) == false){
			compound.setInteger(CLAIM_START_X, (int)Math.floor(player.posX));
			compound.setInteger(CLAIM_START_Y, (int)Math.floor(player.posY));
			compound.setInteger(CLAIM_START_Z, (int)Math.floor(player.posZ));
			compound.setBoolean(CLAIM_STATE, true);
			commandSender.addChatMessage(new ChatComponentText("griefguardian.commands.claimstart"));
		}else{
			compound.setBoolean(CLAIM_STATE, false);
			Vec3 start = Vec3.createVectorHelper(compound.getInteger(CLAIM_START_X), compound.getInteger(CLAIM_START_Y), compound.getInteger(CLAIM_START_Z));
			Vec3 end = Vec3.createVectorHelper((int)Math.floor(player.posX), (int)Math.floor(player.posY), (int)Math.floor(player.posZ));
			if (start.distanceTo(end) < 10){
				commandSender.addChatMessage(new ChatComponentText("griefguardian.commands.claimtoosmall"));
				return;
			}
			
			ActionResults res = ClaimsList.For(commandSender.getEntityWorld()).tryClaimArea(commandSender, start, end);
			commandSender.addChatMessage(new ChatComponentText(res.message));
		}
	}

}
