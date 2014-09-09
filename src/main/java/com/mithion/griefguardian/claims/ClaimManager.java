package com.mithion.griefguardian.claims;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLLog;

/**
 * Contains all claim lists across loaded dimensions.
 * Also responsible for saving and loading the claim data.
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
public class ClaimManager {
	private static HashMap<Integer, ClaimsList> dimensionCache;

	private File saveLocation;

	//Singleton setup
	public static final ClaimManager instance = new ClaimManager();
	private ClaimManager(){
		dimensionCache = new HashMap<Integer, ClaimsList>();
	}
	//

	public void setSaveLocation(File directory){
		saveLocation = directory;
	}

	public ClaimsList getClaimsList(World world){
		if (!dimensionCache.containsKey(world.provider.dimensionId)){
			dimensionCache.put(world.provider.dimensionId, new ClaimsList());
		}
		return dimensionCache.get(world.provider.dimensionId);		
	}

	public String createTeamIdentifier(Team team){
		return String.format("t:%s", team.getRegisteredName());
	}

	public void saveAllClaims(WorldServer world){
		FMLLog.fine("GG >> Saving Claim Data for dimension %d", world.provider.dimensionId);
		ClaimsList list = getClaimsList(world);
		File worldFile = claimSaveFor(world);
		NBTTagCompound compound = new NBTTagCompound();
		list.writeToNBT(compound);
		try {
			CompressedStreamTools.write(compound, worldFile);
		} catch (IOException e) {
			FMLLog.severe("GG >> Unable to save claim data!  Something went wrong!");
			e.printStackTrace();
		}
	}

	public void loadAllClaims(WorldServer world){
		FMLLog.fine("GG >> Loading Claim Data for dimension %d", world.provider.dimensionId);
		ClaimsList list = getClaimsList(world);
		File worldFile = claimSaveFor(world);
		if (worldFile.exists()){
			try{
				NBTTagCompound compound = CompressedStreamTools.read(worldFile);
				list.readFromNBT(compound);
			}catch (Throwable t){
				FMLLog.severe("GG >> Unable to load claim data!  Something went wrong!");
				t.printStackTrace();
			}
		}
	}

	public File claimSaveFor(WorldServer world) {
		String dirName = world.getSaveHandler().getWorldDirectoryName().replace(" ", "");
		Pattern p = Pattern.compile("[\\W]");
		Matcher m = p.matcher(dirName);
		dirName = m.replaceAll(dirName);
		File worldFile = new File (saveLocation.getAbsolutePath() + File.separatorChar + dirName + "DIM" + world.provider.dimensionId + ".dat");
		return worldFile;
	}

}
