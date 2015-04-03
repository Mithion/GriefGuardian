package com.mithion.griefguardian.eventhandlers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.mithion.griefguardian.claims.Claim;
import com.mithion.griefguardian.claims.ClaimsList;
import com.mithion.griefguardian.claims.PermissionsMutex;
import com.mithion.griefguardian.util.AABBUtils;
import com.mithion.griefguardian.util.PlayerDataUtils;
import com.mithion.griefguardian.util.RenderUtils;

public class ClientEventHandler {
	@SubscribeEvent
	public void onRenderWorld(RenderWorldLastEvent event){
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		if (PlayerDataUtils.shouldRenderClaims(p)){
			for (Claim claim : ClaimsList.For(Minecraft.getMinecraft().theWorld).getClaimsForPlayer(p, PermissionsMutex.ALL_FLAGS, true)){
				if (AABBUtils.AABBIsWithinRange(claim.getBounds(), new Vec3(p.posX, p.posY, p.posZ), 32)){
					GL11.glPushMatrix();
					GL11.glTranslated(-(p.prevPosX + (p.posX - p.prevPosX) * event.partialTicks), 
							-(p.prevPosY + (p.posY - p.prevPosY) * event.partialTicks), 
							-(p.prevPosZ + (p.posZ - p.prevPosZ) * event.partialTicks));
					RenderUtils.renderBoundingBox(claim.getBounds(), 0x00FF00, 0x000000);
					GL11.glPopMatrix();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Pre event){
		if (PlayerDataUtils.isAdminInvisible(event.entityPlayer))
			event.setCanceled(true);
	}
}
