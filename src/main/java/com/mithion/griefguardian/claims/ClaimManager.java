package com.mithion.griefguardian.claims;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;

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

	public void saveAllClaims(World world){
		FMLLog.fine("GG >> Saving Claim Data for dimension %d", world.provider.dimensionId);
		ClaimsList list = getClaimsList(world);
		File worldFile = new File (saveLocation.getAbsolutePath() + File.separatorChar + "DIM" + world.provider.dimensionId + ".dat");
		NBTTagCompound compound = new NBTTagCompound();
		list.writeToNBT(compound);
		try {
			CompressedStreamTools.write(compound, worldFile);
		} catch (IOException e) {
			FMLLog.severe("GG >> Unable to save claim data!  Something went wrong!");
			e.printStackTrace();
		}
	}

	public void loadAllClaims(World world){
		FMLLog.fine("GG >> Loading Claim Data for dimension %d", world.provider.dimensionId);
		ClaimsList list = getClaimsList(world);
		File worldFile = new File (saveLocation.getAbsolutePath() + File.separatorChar + "DIM" + world.provider.dimensionId + ".dat");
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

	public void unloadClaimsForWorld(World world) {
		saveAllClaims(world);
		dimensionCache.remove(world.provider.dimensionId);
	}

}
