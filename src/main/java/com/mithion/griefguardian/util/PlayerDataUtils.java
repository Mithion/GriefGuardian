package com.mithion.griefguardian.util;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerDataUtils {
	private static final String RENDER_CLAIMS = "gg_render_claims";
	private static final String MASTER_ACL = "gg_mast_acl";
	
	private static final int ADMIN_INVIS_ON = 1;
	private static final int ADMIN_INVIS_OFF = 0;
	
	private static final UUID adminInvisAttr = UUID.fromString("8a273900-3699-11e4-8510-0800200c9a66");
	private static final IAttribute adminInvis = new RangedAttribute("admin_invisibility", ADMIN_INVIS_OFF, ADMIN_INVIS_OFF, ADMIN_INVIS_ON).setShouldWatch(true);	
	private static final AttributeModifier adminInvisOn = new AttributeModifier(adminInvisAttr, "admin_invis_on", ADMIN_INVIS_ON, 0);
	
	public static void setRenderClaimsData(EntityPlayer player, boolean renderClaims){
		player.getEntityData().setBoolean(RENDER_CLAIMS, renderClaims);
	}
	
	public static boolean shouldRenderClaims(EntityPlayer player){
		return player.getEntityData().getBoolean(RENDER_CLAIMS);
	}
	
	public static void setAdminInvisiblity(EntityPlayer player, boolean invisible){
		IAttributeInstance inst = player.getAttributeMap().getAttributeInstance(adminInvis);
		inst.removeModifier(adminInvisOn);
		if (invisible){
			inst.applyModifier(adminInvisOn);
		}
	}
	
	public static boolean isAdminInvisible(EntityPlayer player){
		IAttributeInstance inst = player.getAttributeMap().getAttributeInstance(adminInvis);
		if (inst == null)
			return false;
		double val = inst.getAttributeValue();
		return val == ADMIN_INVIS_ON;
	}

	public static void registerAttributes(EntityPlayer player){
		if (player.getAttributeMap().getAttributeInstance(adminInvis) == null)
			player.getAttributeMap().registerAttribute(adminInvis);
	}

	public static boolean hasMasterACL(EntityPlayer player){
		return player.getEntityData().getBoolean(MASTER_ACL);
	}
	
	public static void setMasterACL(EntityPlayer player, boolean master){
		player.getEntityData().setBoolean(MASTER_ACL, master);
	}
}
