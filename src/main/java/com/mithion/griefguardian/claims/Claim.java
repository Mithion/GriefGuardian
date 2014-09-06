package com.mithion.griefguardian.claims;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.network.ByteBufUtils;

/**
 * Contains all data fpr one given claim.
 * This includes the location, size, owner, and ACLs.
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
public class Claim{
	private String claimOwner;		
	private HashMap<String, PermissionsMutex> claimPermissions;
	private AxisAlignedBB claimBounds;

	public static final String EVERYONE = "I:Everyone";

	public Claim(String owner, AxisAlignedBB bounds){
		this.claimOwner = owner;
		this.claimBounds = bounds;
		claimPermissions = new HashMap<String, PermissionsMutex>();
	}
	
	public Claim(ByteBuf buf){
		claimPermissions = new HashMap<String, PermissionsMutex>();
		readFromByteBuf(buf);
	}
	
	public Claim(NBTTagCompound compound){
		claimPermissions = new HashMap<String, PermissionsMutex>();
		readFromNBT(compound);
	}

	/**
	 * Gets the claim owner
	 */
	public String getOwner(){
		return claimOwner;
	}
	
	/**
	 * Resolves the permissions mutex for the given identifier, or creates a new one if it didn't exist.
	 */
	private PermissionsMutex getPermissionsMutex(String identifier){
		if (!claimPermissions.containsKey(identifier)){
			claimPermissions.put(identifier, new PermissionsMutex());
		}
		PermissionsMutex mutex = claimPermissions.get(identifier);
		return mutex;
	}

	/**
	 * Adds permissions to the mutex
	 * @param identifier The identifier of the mutex.  Can be a player name, a predefined constant in this class, or a team.  The ClaimManager has a helper function to create team identifiers.
	 * @param flags The flags to grant permission for.
	 */
	public void addPermission(String identifier, int flags){			
		getPermissionsMutex(identifier).setFlags(flags);
	}

	/**
	 * Removes permissions from the mutex
	 * @param identifier The identifier of the mutex.  Can be a player name, a predefined constant in this class, or a team.  The ClaimManager has a helper function to create team identifiers.
	 * @param flags The flags to grant permission for.
	 */
	public void removePermission(String identifier, int flags){
		getPermissionsMutex(identifier).clearFlags(flags);
	}

	/**
	 * Removes all permissions for the specified identifier from the mutex.
	 */
	public void removeAllPermission(String identifier){
		claimPermissions.remove(identifier);
	}

	/**
	 * Checks if the specified xyz coordinate falls inside this claim
	 */
	public boolean testBounds(int x, int y, int z){
		Vec3 vec = Vec3.createVectorHelper(x, y, z);
		boolean inside = claimBounds.isVecInside(vec);
		return inside;
	}

	/**
	 * Checkes if the specified bounding box intersects this claim
	 */
	public boolean testBounds(AxisAlignedBB bb){
		return this.claimBounds.intersectsWith(bb);
	}

	/**
	 * Checkes if the specified claim intersects this claim
	 */
	public boolean testBounds(Claim claim){
		return this.claimBounds.intersectsWith(claim.claimBounds);
	}

	/**
	 * Checks if the given identifier is allowed the current action in this claim.
	 * See PermissionsMutex for valid actions list
	 */
	public boolean actionIsPermitted(String identifier, int action){
		if (identifier.equals(claimOwner))
			return true;
		return getPermissionsMutex(identifier).hasAllFlags(action);
	}
	
	/**
	 * Writes this claim to a byte buffer
	 */
	public void writeToByteBuf(ByteBuf buf){
		ByteBufUtils.writeUTF8String(buf, claimOwner);
		buf.writeDouble(claimBounds.minX);
		buf.writeDouble(claimBounds.minY);
		buf.writeDouble(claimBounds.minZ);
		buf.writeDouble(claimBounds.maxX);
		buf.writeDouble(claimBounds.maxY);
		buf.writeDouble(claimBounds.maxZ);
		buf.writeInt(claimPermissions.size());
		for (String s : claimPermissions.keySet()){
			ByteBufUtils.writeUTF8String(buf, s);
			buf.writeInt(claimPermissions.get(s).getMask());
		}
	}
	
	/**
	 * Reads a claim from a byte buffer
	 */
	public void readFromByteBuf(ByteBuf buf){
		claimOwner = ByteBufUtils.readUTF8String(buf);
		claimBounds = AxisAlignedBB.getBoundingBox(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(),buf.readDouble(), buf.readDouble());
		int numPermRecords = buf.readInt();
		for (int i = 0; i < numPermRecords; ++i){
			String s = ByteBufUtils.readUTF8String(buf);
			int mask = buf.readInt();
			claimPermissions.put(s, new PermissionsMutex(mask));
		}
	}

	
	/**
	 * Gets the current permissions mask
	 */
	public int getPermissionMask(String identifier) {
		if (!this.claimPermissions.containsKey(identifier))
			return 0;
		return this.claimPermissions.get(identifier).getMask();
	}

	public AxisAlignedBB getBounds() {
		return claimBounds.copy();
	}

	public void setClaimOwner(String newOwner) {
		this.claimOwner = newOwner;
	}

	public void writeToNBT(NBTTagCompound comp) {
		comp.setString("claim_owner", claimOwner);
		comp.setDouble("bounds_min_x", claimBounds.minX);
		comp.setDouble("bounds_min_y", claimBounds.minY);
		comp.setDouble("bounds_min_z", claimBounds.minZ);
		comp.setDouble("bounds_max_x", claimBounds.maxX);
		comp.setDouble("bounds_max_y", claimBounds.maxY);
		comp.setDouble("bounds_max_z", claimBounds.maxZ);
		
		NBTTagList tagList = new NBTTagList();
		for (String s : claimPermissions.keySet()){
			NBTTagCompound permissionCompound = new NBTTagCompound();
			permissionCompound.setString("permission_identifier", s);
			permissionCompound.setInteger("permission_mask", claimPermissions.get(s).getMask());
			
			tagList.appendTag(permissionCompound);
		}
		
		comp.setTag("permission_records", tagList);
	}
	
	public void readFromNBT(NBTTagCompound comp){
		claimOwner = comp.getString("claim_owner");
		claimBounds = AxisAlignedBB.getBoundingBox(
				comp.getDouble("bounds_min_x"), 
				comp.getDouble("bounds_min_y"), 
				comp.getDouble("bounds_min_z"), 
				comp.getDouble("bounds_max_x"), 
				comp.getDouble("bounds_max_y"), 
				comp.getDouble("bounds_max_z"));
		
		NBTTagList tagList = comp.getTagList("permission_records", 10);
		for (int i = 0; i < tagList.tagCount(); ++i){
			NBTTagCompound permissionCompound = tagList.getCompoundTagAt(i);
			String identifier = permissionCompound.getString("permission_identifier");
			int mask = permissionCompound.getInteger("permission_mask");
			claimPermissions.put(identifier, new PermissionsMutex(mask));
		}
	}
}