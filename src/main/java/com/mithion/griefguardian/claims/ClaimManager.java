package com.mithion.griefguardian.claims;

import java.util.HashMap;

import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;

public class ClaimManager {
	private static HashMap<Integer, ClaimsList> dimensionCache;
	
	//Singleton setup
	public static final ClaimManager instance = new ClaimManager();
	private ClaimManager(){
		dimensionCache = new HashMap<Integer, ClaimsList>();
	}
	//
	
	public ClaimsList getClaimsList(World world){
		if (!dimensionCache.containsKey(world.provider.dimensionId)){
			dimensionCache.put(world.provider.dimensionId, new ClaimsList());
		}
		return dimensionCache.get(world.provider.dimensionId);		
	}
	
	public String createTeamIdentifier(Team team){
		return String.format("t:%s", team.getRegisteredName());
	}
	
}
