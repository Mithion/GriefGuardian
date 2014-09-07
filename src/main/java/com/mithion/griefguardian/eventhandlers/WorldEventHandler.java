package com.mithion.griefguardian.eventhandlers;

import com.mithion.griefguardian.claims.ClaimManager;

import net.minecraftforge.event.world.WorldEvent;
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
}
