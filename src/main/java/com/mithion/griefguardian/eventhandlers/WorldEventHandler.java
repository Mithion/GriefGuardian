package com.mithion.griefguardian.eventhandlers;

import java.util.Date;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.claims.ClaimManager;
import com.mithion.griefguardian.util.PlayerDataUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class WorldEventHandler {
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event){
		if (!event.world.isRemote){
			ClaimManager.instance.saveAllClaims((WorldServer)event.world);
			if (event.world.provider.dimensionId == 0)
				PlayerDataUtils.saveAllGlobalWarpPoints();
		}
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event){
		if (!event.world.isRemote){
			ClaimManager.instance.loadAllClaims((WorldServer)event.world);
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event){
		if (event.entity instanceof EntityPlayer){
			PlayerDataUtils.registerAttributes((EntityPlayer) event.entity);
		}
	}
	
	@SubscribeEvent
	public void onEntityConnect(PlayerEvent.PlayerLoggedInEvent event){
		if (!(event.player instanceof EntityPlayerMP))
			return;
		long time = MinecraftServer.getSystemTimeMillis();
		EntityPlayerMP player = (EntityPlayerMP)event.player;
		long unbanTime = GriefGuardian._dal.getUnbanTime(player);
		if (unbanTime > time){
			if (unbanTime == Long.MAX_VALUE)
				player.playerNetServerHandler.kickPlayerFromServer("You are permanently banned from this server.");
			else
				player.playerNetServerHandler.kickPlayerFromServer(String.format("You are currently banned from this server.  Your temporary ban will expire at %s.", new Date(unbanTime).toGMTString()));
		}
	}
}
