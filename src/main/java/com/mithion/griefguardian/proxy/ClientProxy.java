package com.mithion.griefguardian.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.mithion.griefguardian.eventhandlers.ClientEventHandler;
import com.mithion.griefguardian.eventhandlers.SpamGuardEventHandler;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerHandlers() {	
		super.registerHandlers();
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}
	
	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
}
