package com.mithion.griefguardian.eventhandlers;

import net.minecraft.command.CommandBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.api.Actions;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

/**
 * This class is for logging events that don't belong in the {@link ClaimGuardEventHandler}.
 * Make sure you check that class first before adding a new event here!
 * @author Mithion
 *
 */
public class ActionLogEventHandler {
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event){

		if (event.entity.worldObj.isRemote)
			return;

		EntityPlayerMP source = null;
		EntityLivingBase death = event.entityLiving;

		if (event.source.getSourceOfDamage() != null && event.source.getSourceOfDamage() instanceof EntityPlayerMP){
			source = (EntityPlayerMP)event.source.getSourceOfDamage();
		}		

		//we only want things a player kills, or when a player dies.
		if (source == null && !(death instanceof EntityPlayerMP)){
			return;
		}		

		if (death instanceof EntityPlayerMP){ //if the target is a player			
			if (source != null){//if the player was killed by another player
				GriefGuardian._dal.logAction(
						(EntityPlayerMP)death, 
						Actions.PLAYER_KILL, 
						(int)Math.floor(event.entity.posX), 
						(int)Math.floor(event.entity.posY), 
						(int)Math.floor(event.entity.posZ), 
						source.getCurrentEquippedItem(), 
						event.source.func_151519_b(death).getUnformattedText());
			}else{ //if the player was killed by something else
				GriefGuardian._dal.logAction(
						(EntityPlayerMP)death, 
						Actions.PLAYER_DEATH, 
						(int)Math.floor(event.entity.posX), 
						(int)Math.floor(event.entity.posY), 
						(int)Math.floor(event.entity.posZ), 
						null, 
						event.source.func_151519_b(death).getUnformattedText());
			}
		}else{ //target is a mob
			GriefGuardian._dal.logAction(
					source, 
					Actions.MOB_KILL, 
					(int)Math.floor(event.entity.posX), 
					(int)Math.floor(event.entity.posY), 
					(int)Math.floor(event.entity.posZ), 
					source.getCurrentEquippedItem(), 
					"");
		}
	}
	
	@SubscribeEvent
	public void onChatMessage(ServerChatEvent event){		
		GriefGuardian._dal.logAction(
				event.player, 
				Actions.CHAT, 
				(int)Math.floor(event.player.posX), 
				(int)Math.floor(event.player.posY), 
				(int)Math.floor(event.player.posZ), 
				null, 
				event.message);
	}
	
	@SubscribeEvent
	public void onServerCommand(CommandEvent event){
		EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(event.sender);
		String cmd = event.command.getCommandName();
		for (String s : event.parameters)
			cmd += " " + s;
		GriefGuardian._dal.logAction(
				player, 
				Actions.COMMAND, 
				(int)Math.floor(player.posX), 
				(int)Math.floor(player.posY), 
				(int)Math.floor(player.posZ), 
				null, 
				cmd
				);
	}
	
	@SubscribeEvent
	public void onEntityConnect(PlayerEvent.PlayerLoggedInEvent event){
		if (event.player instanceof EntityPlayerMP)
		GriefGuardian._dal.logAction(
				(EntityPlayerMP)event.player, 
				Actions.CONNECT, 
				(int)Math.floor(event.player.posX), 
				(int)Math.floor(event.player.posY), 
				(int)Math.floor(event.player.posZ), 
				null, 
				""
				);
	}
	
	@SubscribeEvent
	public void onEntityDisconnect(PlayerEvent.PlayerLoggedOutEvent event){
		if (event.player instanceof EntityPlayerMP)
			GriefGuardian._dal.logAction(
					(EntityPlayerMP)event.player, 
					Actions.DISCONNECT, 
					(int)Math.floor(event.player.posX), 
					(int)Math.floor(event.player.posY), 
					(int)Math.floor(event.player.posZ), 
					null, 
					""
					);
	}
}
