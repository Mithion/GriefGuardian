package com.mithion.griefguardian.network;

import net.minecraft.client.Minecraft;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.claims.ClaimsList;

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * Handles the sync claims message.
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
public class PacketSyncMessageHandler implements IMessageHandler<PacketSyncClaims, PacketSyncClaims>{

	@Override
	public PacketSyncClaims onMessage(PacketSyncClaims message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT){
			if (message.isStopRenderPacket()){
				GriefGuardian.proxy.setRenderClaims(false);
			}else{
				GriefGuardian.proxy.setRenderClaims(true);
				ClaimsList.For(GriefGuardian.proxy.getClientWorld()).loadFromSyncPacket(message);
			}
		}
		return null;
	}

}
