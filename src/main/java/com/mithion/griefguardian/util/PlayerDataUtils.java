package com.mithion.griefguardian.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerDataUtils {
	private static final String RENDER_CLAIMS = "gg_render_claims";
	
	public static void setRenderClaimsData(EntityPlayer player, boolean renderClaims){
		player.getEntityData().setBoolean(RENDER_CLAIMS, renderClaims);
	}
	
	public static boolean shouldRenderClaims(EntityPlayer player){
		return player.getEntityData().getBoolean(RENDER_CLAIMS);
	}
}
