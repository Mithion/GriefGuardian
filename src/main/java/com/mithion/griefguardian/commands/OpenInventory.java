package com.mithion.griefguardian.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.mithion.griefguardian.util.OfflineInventory;
import com.mithion.griefguardian.util.PlayerDataUtils;

public class OpenInventory extends CommandBase {

	@Override
	public String getCommandName() {
		return "openinv";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/openinv <playername>";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) throws WrongUsageException {
		if (args.length != 1)
			throw new WrongUsageException(getCommandUsage(commandSender));
		
		
		EntityPlayerMP target = null;
		EntityPlayerMP operator;
		try {
			operator = getCommandSenderAsPlayer(commandSender);
		} catch (PlayerNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		try{
			target = getPlayer(commandSender, args[0]);
		}catch(Throwable t){
			//don't care
		}
		if (target == null){
			NBTTagCompound compound = PlayerDataUtils.loadOfflinePlayerData(args[0]);
			if (compound != null)
				OfflineInventory.open(operator, new OfflineInventory(compound, args[0]), args[0]);
		}else{
			operator.displayGUIChest(target.inventory);
		}
	}

}
