package com.mithion.griefguardian.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLLog;

import com.mojang.authlib.GameProfile;

public class PlayerDataUtils {
	private static final String RENDER_CLAIMS = "gg_render_claims";
	private static final String MASTER_ACL = "gg_mast_acl";
	private static final String WARP_POINTS = "warp_points";

	private static NBTTagCompound globalWarpPoints;

	private static final double ADMIN_INVIS_ON = 1;
	private static final double ADMIN_INVIS_OFF = 0;

	private static final UUID adminInvisAttr = UUID.fromString("8a273900-3699-11e4-8510-0800200c9a66");
	private static final IAttribute adminInvis = new RangedAttribute(null, "admin_invisibility", ADMIN_INVIS_OFF, ADMIN_INVIS_OFF, ADMIN_INVIS_ON).setShouldWatch(true);
	private static final AttributeModifier adminInvisOn = new AttributeModifier(adminInvisAttr, "admin_invis_on", ADMIN_INVIS_ON, 0);

	private static File instanceDirectory;

	public static void setInstanceDir(File instance){
		if (instanceDirectory == null)
			instanceDirectory = instance;
	}

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

	public static NBTTagCompound loadOfflinePlayerData(String playerName){

		GameProfile gp = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(playerName);

		String basePath = instanceDirectory.getAbsolutePath() + File.separatorChar;
		if (MinecraftServer.getServer().isSinglePlayer())
			basePath += "saves" + File.separatorChar;
		File playerNBT = new File(basePath + MinecraftServer.getServer().getFolderName() + File.separatorChar + "playerdata" + File.separatorChar + gp.getId().toString() + ".dat");
		if (!playerNBT.exists())
			return null;
		try {
			return CompressedStreamTools.readCompressed(new FileInputStream(playerNBT));
		} catch (IOException e) {
			FMLLog.severe("GG >> Unable to load offline inventory!");
			e.printStackTrace();
			return null;
		}
	}

	public static void SaveOfflinePlayerData(String playerName, NBTTagCompound compound){
		GameProfile gp = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(playerName);

		String basePath = instanceDirectory.getAbsolutePath() + File.separatorChar;
		if (MinecraftServer.getServer().isSinglePlayer())
			basePath += "saves" + File.separatorChar;
		File playerNBT = new File(basePath + MinecraftServer.getServer().getFolderName() + File.separatorChar + "playerdata" + File.separatorChar + gp.getId().toString() + ".dat");
		try {
			CompressedStreamTools.writeCompressed(compound, new FileOutputStream(playerNBT));
		} catch (IOException e) {
			FMLLog.severe("GG >> Unable to save offline inventory changes!");
			e.printStackTrace();
		}
	}

	public static WarpPoint findLocalWarpPoint(EntityPlayerMP player, String wpName){
		NBTTagCompound warpPoints = (NBTTagCompound) player.getEntityData().getTag(WARP_POINTS);
		if (warpPoints == null)
			return null;
		NBTTagCompound compound = (NBTTagCompound) warpPoints.getTag(wpName);
		if (compound == null)
			return null;

		return WarpPoint.FromNBT(compound);
	}

	public static void saveLocalWarpPoint(EntityPlayerMP player, String wpName, WarpPoint point){
		if (!player.getEntityData().hasKey(WARP_POINTS))
			player.getEntityData().setTag(WARP_POINTS, new NBTTagCompound());

		((NBTTagCompound)player.getEntityData().getTag(WARP_POINTS)).setTag(wpName, WarpPoint.ToNBT(point));
	}

	public static WarpPoint findGlobalWarpPoint(String wpName){
		if (globalWarpPoints == null) //load on demand
			loadAllGlobalWarpPoints();

		NBTTagCompound compound = (NBTTagCompound) globalWarpPoints.getTag(wpName);
		if (compound == null)
			return null;

		return WarpPoint.FromNBT(compound);
	}

	public static void saveGlobalWarpPoint(String wpName, WarpPoint point){
		if (globalWarpPoints == null) //load on demand
			loadAllGlobalWarpPoints();

		globalWarpPoints.setTag(wpName, WarpPoint.ToNBT(point));
	}

	public static void loadAllGlobalWarpPoints(){
		try {			
			File file = new File(MinecraftServer.getServer().worldServers[0].getSaveHandler().getWorldDirectory().getAbsolutePath() + File.separatorChar + "ClaimData" + File.separatorChar + "global_warp_points.dat");
			if (!file.exists()){ // nothing to load
				globalWarpPoints = new NBTTagCompound();
				return;
			}
			globalWarpPoints = CompressedStreamTools.read(file);
		} catch (IOException e) {
			FMLLog.severe("GG >> Unable to load global warp points list!");
			globalWarpPoints = new NBTTagCompound();
			e.printStackTrace();
		}
	}

	public static void saveAllGlobalWarpPoints(){
		if (globalWarpPoints == null) //nothing to save
			return;

		try {
			File file = new File(MinecraftServer.getServer().worldServers[0].getSaveHandler().getWorldDirectory().getAbsolutePath() + File.separatorChar + "ClaimData" + File.separatorChar);
			if (!file.exists()){
				file.mkdirs();
			}
			file = new File(file.getAbsolutePath() + "global_warp_points.dat");
			CompressedStreamTools.write(globalWarpPoints, file);
		} catch (IOException e) {
			FMLLog.severe("GG >> Unable to save global warp points list!");
			e.printStackTrace();
		}
	}
}
