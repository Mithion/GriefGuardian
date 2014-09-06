package com.mithion.griefguardian.proxy;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.mithion.griefguardian.eventhandlers.ClaimGuardEventHandler;
import com.mithion.griefguardian.eventhandlers.CommandGuardEventHandler;
import com.mithion.griefguardian.eventhandlers.SpamGuardEventHandler;
import com.mithion.griefguardian.eventhandlers.WorldEventHandler;

public class CommonProxy {
	
	public void registerHandlers(){
		MinecraftForge.EVENT_BUS.register(new ClaimGuardEventHandler());
    	MinecraftForge.EVENT_BUS.register(new CommandGuardEventHandler());
    	MinecraftForge.EVENT_BUS.register(new SpamGuardEventHandler());
    	MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
	}
	
	public World getClientWorld(){
		return null;
	}

	public void setRenderClaims(boolean b) {
				
	}
}
