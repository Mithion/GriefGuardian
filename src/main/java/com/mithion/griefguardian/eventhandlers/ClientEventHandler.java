package com.mithion.griefguardian.eventhandlers;

import org.lwjgl.opengl.GL11;

import com.mithion.griefguardian.claims.Claim;
import com.mithion.griefguardian.claims.ClaimsList;
import com.mithion.griefguardian.claims.PermissionsMutex;
import com.mithion.griefguardian.util.AABBUtils;
import com.mithion.griefguardian.util.RenderUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ClientEventHandler {
	@SubscribeEvent
	public void onRenderWorld(RenderWorldLastEvent event){
		EntityPlayer p = Minecraft.getMinecraft().thePlayer;
		for (Claim claim : ClaimsList.For(Minecraft.getMinecraft().theWorld).getClaimsForPlayer(p, PermissionsMutex.ALL_FLAGS, true)){
			if (AABBUtils.AABBIsWithinRange(claim.getBounds(), Vec3.createVectorHelper(p.posX, p.posY, p.posZ), 32)){
				GL11.glPushMatrix();
				GL11.glTranslated(-(p.prevPosX + (p.posX - p.prevPosX) * event.partialTicks), 
						-(p.prevPosY + (p.posY - p.prevPosY) * event.partialTicks), 
						-(p.prevPosZ + (p.posZ - p.prevPosZ) * event.partialTicks));
				RenderUtils.renderBoundingBox(claim.getBounds(), 0x00FF00, 0xFFFFFF);
				GL11.glPopMatrix();
			}
		}
	}
}
