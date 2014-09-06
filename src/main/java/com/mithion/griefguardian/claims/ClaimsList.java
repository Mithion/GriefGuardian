package com.mithion.griefguardian.claims;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;

import com.mithion.griefguardian.network.PacketSyncClaims;

import cpw.mods.fml.common.network.ByteBufUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ClaimsList {
	private HashMap<String, ArrayList<Claim>> claimCache;	

	public ClaimsList(){
		claimCache = new HashMap<String, ArrayList<Claim>>();
	}

	public enum ActionResults{
		SUCCESS("griefguardian.actionresult.success"),
		DENIED("griefguardian.actionresult.denied"),
		NO_CLAIM_PRESENT("griefguardian.actionresult.noclaim"),
		CLAIM_INTERSECTS("griefguardian.actionresults.claimintersects");

		public final String message;

		ActionResults(String message){
			this.message = message;
		}
	}

	/**
	 * Convenience access function.  Queries the Claim manager for the list pertaining to the specified world.
	 * @param world The world to get the claims list for
	 */
	public static ClaimsList For(World world){
		return ClaimManager.instance.getClaimsList(world);
	}

	/**
	 * Checks if the specified action is allowed at the given coordinates for the specified player.
	 * @param player The player attempting to perform the action
	 * @param action The action being attempted.  See {@link #PermissionsMutex} for available actions.
	 * @param x The x coordinate of the action
	 * @param y The y coordinate of the action
	 * @param z The z coordinate of the action
	 * @return True if the action is allowed, otherwise false.
	 */
	public boolean actionIsTrusted(EntityPlayer player, int action, double x, double y, double z){
		return actionIsTrusted(player, action, (int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
	}

	/**
	 * Checks if the specified action is allowed at the given coordinates for the specified player.
	 * @param player The player attempting to perform the action
	 * @param action The action being attempted.  See {@link #PermissionsMutex} for available actions.
	 * @param x The x coordinate of the action
	 * @param y The y coordinate of the action
	 * @param z The z coordinate of the action
	 * @return True if the action is allowed, otherwise false.
	 */
	public boolean actionIsTrusted(EntityPlayer player, int action, int x, int y, int z){
		//is the area claimed?
		Claim claim = getClaimForCoords(x, y, z);
		if (claim == null) //nope
			return true;

		//check global permissions
		if (claim.actionIsPermitted(Claim.EVERYONE, action))
			return true;

		//check player name
		if (claim.actionIsPermitted(player.getCommandSenderName(), action))
			return true;

		//check player teams
		if (player.getTeam() != null && claim.actionIsPermitted(ClaimManager.instance.createTeamIdentifier(player.getTeam()), action))
			return true;

		//if we're here, then no access for you.
		return false;
	}

	public ActionResults clearClaimPermissions(ICommandSender requester, String identifier, int x, int y, int z){
		//is the area claimed?
		Claim claim = getClaimForCoords(x, y, z);
		if (claim == null) //nope
			return ActionResults.NO_CLAIM_PRESENT;

		//can the requester modify the ACL?
		if (!claim.actionIsPermitted(requester.getCommandSenderName(), PermissionsMutex.MODIFY_ACL))
			return ActionResults.DENIED;

		//do eet
		claim.removeAllPermission(identifier);
		return ActionResults.SUCCESS;
	}

	public ActionResults addClaimPermissions(ICommandSender requester, String identifier, int permissionMask, int x, int y, int z){
		//is the area claimed?
		Claim claim = getClaimForCoords(x, y, z);
		if (claim == null) //nope
			return ActionResults.NO_CLAIM_PRESENT;

		//can the requester modify the ACL?
		if (!claim.actionIsPermitted(requester.getCommandSenderName(), PermissionsMutex.MODIFY_ACL))
			return ActionResults.DENIED;

		//do eet
		claim.addPermission(identifier, permissionMask);
		return ActionResults.SUCCESS;
	}

	public ActionResults removeClaimPermissions(ICommandSender requester, String identifier, int permissionMask, int x, int y, int z){
		//is the area claimed?
		Claim claim = getClaimForCoords(x, y, z);
		if (claim == null) //nope
			return ActionResults.NO_CLAIM_PRESENT;

		//can the requester modify the ACL?
		if (!claim.actionIsPermitted(requester.getCommandSenderName(), PermissionsMutex.MODIFY_ACL))
			return ActionResults.DENIED;

		//do eet
		claim.removePermission(identifier, permissionMask);
		return ActionResults.SUCCESS;
	}

	public ActionResults tryClaimArea(ICommandSender requester, Vec3 start, Vec3 end){
		AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(
				Math.min(start.xCoord, end.xCoord) - 0.5f, 
				Math.min(start.yCoord, end.yCoord) - 0.5f, 
				Math.min(start.zCoord, end.zCoord) - 0.5f, 
				Math.max(start.xCoord, end.xCoord) + 0.5f, 
				Math.max(start.yCoord, end.yCoord) + 0.5f, 
				Math.max(start.zCoord, end.zCoord) + 0.5f);
		Claim claim = getIntersectingClaim(bb);
		if (claim != null)
			return ActionResults.CLAIM_INTERSECTS;

		Claim newClaim = new Claim(requester.getCommandSenderName(), bb);
		if (!claimCache.containsKey(requester.getCommandSenderName()))
			claimCache.put(requester.getCommandSenderName(), new ArrayList<Claim>());
		claimCache.get(requester.getCommandSenderName()).add(newClaim);
		return ActionResults.SUCCESS;
	}

	public ActionResults tryDeleteClaim(EntityPlayer player){		
		Claim claim = getClaimForCoords((int)Math.floor(player.posX), (int)Math.floor(player.posY), (int)Math.floor(player.posZ));
		if (claim == null)
			return ActionResults.NO_CLAIM_PRESENT;

		if (!actionIsTrusted(player, PermissionsMutex.DELETE_CLAIM, player.posX, player.posY, player.posZ))
			return ActionResults.DENIED;

		claimCache.get(player.getCommandSenderName()).remove(claim);
		return ActionResults.SUCCESS;
	}

	public ActionResults tryTransferClaim(EntityPlayer player, String newOwner) {
		Claim claim = getClaimForCoords((int)Math.floor(player.posX), (int)Math.floor(player.posY), (int)Math.floor(player.posZ));
		if (claim == null)
			return ActionResults.NO_CLAIM_PRESENT;

		if (!actionIsTrusted(player, PermissionsMutex.MODIFY_ACL, player.posX, player.posY, player.posZ))
			return ActionResults.DENIED;

		claim.setClaimOwner(newOwner);
		return ActionResults.SUCCESS;
	}

	public void loadFromSyncPacket(PacketSyncClaims pkt){
		HashMap<String, ArrayList<Claim>> data = pkt.getData();
		claimCache.clear();
		for (String s : data.keySet()){
			if (!claimCache.containsKey(s))
				claimCache.put(s, new ArrayList<Claim>());
			claimCache.put(s, (ArrayList<Claim>) data.get(s).clone());
		}
	}

	public PacketSyncClaims createSyncMessage(EntityPlayer player){
		ArrayList<Claim> claims = getClaimsForPlayer(player, PermissionsMutex.ALL_FLAGS, true);
		PacketSyncClaims pkt = new PacketSyncClaims();
		for (Claim claim : claims)
			pkt.addBoundingBox(player.getCommandSenderName(), claim);

		return pkt;
	}

	private Claim getClaimForCoords(int x, int y, int z){
		for (ArrayList<Claim> claimList : claimCache.values()){
			for (Claim claim : claimList){
				if (claim.testBounds(x, y, z))
					return claim;
			}
		}
		return null;
	}

	private Claim getIntersectingClaim(AxisAlignedBB bb){
		for (ArrayList<Claim> claimList : claimCache.values()){
			for (Claim claim : claimList){
				if (claim.testBounds(bb))
					return claim;
			}
		}
		return null;
	}

	/**
	 * Returns any claim the player owns or has the specified permission in
	 * @param player The player to check agains
	 * @param permissionTests The permissions to check
	 * @param any If true, claims where the player has any of the specified permissions will be included.  If false, the player has to have all specified permissions in a claim for it to be included.
	 */
	public ArrayList<Claim> getClaimsForPlayer(EntityPlayer player, int permissionTests, boolean any){
		ArrayList<Claim> claims = new ArrayList<Claim>();
		for (String s : claimCache.keySet()){
			if (s.equals(player.getCommandSenderName())){
				claims.addAll(claimCache.get(s));
			}else{
				for (Claim claim : claimCache.get(s)){
					if ( (any && PermissionsMutex.checkAnyFlags(claim.getPermissionMask(player.getCommandSenderName()), permissionTests)) ||
							(!any && PermissionsMutex.checkAllFlags(claim.getPermissionMask(player.getCommandSenderName()), permissionTests))){
						claims.add(claim);
					}
				}
			}
		}

		return claims;
	}
}
