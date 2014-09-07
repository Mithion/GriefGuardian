package com.mithion.griefguardian.eventhandlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.mithion.griefguardian.claims.ClaimManager;
import com.mithion.griefguardian.util.PlayerDataUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WorldEventHandler {
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event){
		if (!event.world.isRemote)
			ClaimManager.instance.saveAllClaims(event.world);
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event){
		if (!event.world.isRemote)
			ClaimManager.instance.loadAllClaims(event.world);
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event){
		if (event.entity instanceof EntityPlayer){
			PlayerDataUtils.registerAttributes((EntityPlayer) event.entity);
		}
	}
}
