package com.mithion.griefguardian.claims;

/**
 * This is the ACL management class - it contains a single bitflag
 * which determines what actions are allowed.  One of these exists
 * for each permissions group in each claim.
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
public class PermissionsMutex {
	
	public static final int BREAK_BLOCKS = 0x1;
	public static final int PLACE_BLOCKS = 0x2;
	public static final int USE_ITEMS = 0x4;
	public static final int HARM_CREATURES = 0x8;
	public static final int HARM_PLAYERS = 0x10;
	public static final int HARM_MONSTERS = 0x20;
	public static final int PICKUP_ITEMS = 0x40;
	public static final int PICKUP_XP = 0x80;
	public static final int DROP_ITEMS = 0x100;
	public static final int ENTITY_INTERACT = 0x200;
	public static final int USE_BONEMEAL = 0x400;
	public static final int OPEN_CONTAINERS = 0x800;
	public static final int MODIFY_ACL = 0x1000;
	public static final int DELETE_CLAIM = 0x2000;
	
	public static final int ALL_FLAGS = Integer.MAX_VALUE;
	public static final int INTEGER_BIT_LENGTH = 32;
	
	private int mask;
	
	public PermissionsMutex(){
		
	}
	
	public PermissionsMutex(int mask){
		this.mask = mask;
	}
	
	/**
	 * Sets all flags as active.  Will not clear other flags.
	 * @param flagMask The mask of flags to set
	 */
	public void setFlags(int flagMask){
		this.mask |= flagMask;
	}
	
	/**
	 * Clears all specified flags.  Non-specified flags will not be altered.
	 * @param flagMask The mask of flags to clear
	 */
	public void clearFlags(int flagMask){
		this.mask &= ~flagMask;
	}
	
	/**
	 * Convenience function to clear all flags
	 */
	public void clearAllFlags(){
		this.mask = 0;
	}
	
	/**
	 * Convenience function to set all flags
	 */
	public void setAllFlags(){
		this.mask = ALL_FLAGS;
	}
	
	/**
	 * Sets flag mask to specified mask.  Overwrites existing flags.
	 * @param flagMask The mask to set
	 */
	public void replaceFlags(int flagMask){
		this.mask = flagMask;
	}
	
	/**
	 * Checks to see if the flag mask has all of the specified flags
	 * @param flagMask The mask of flags to check
	 */
	public boolean hasAllFlags(int flagMask){
		return (this.mask & flagMask) == flagMask;
	}	
	
	/**
	 * Checks to see if the flag mask has any of the specified flags
	 * @param flagMask The mask of flags to check
	 */
	public boolean hasAnyFlags(int flagMask){
		return (this.mask & flagMask) > 0;
	}	
	
	
	/**
	 * Checks to see if the flag mask has all of the specified flags
	 * @param mask The mask to use in the check
	 * @param flagMask The mask of flags to check
	 */
	public static boolean checkAllFlags(int mask, int flags){
		return (mask & flags) == flags;
	}
	
	/**
	 * Checks to see if any of the passed in flag mask bits are set on this mask.
	 * @param mask The mask to use in the check
	 * @param flagMask The mask of flags to check
	 */
	public static boolean checkAnyFlags(int mask, int flags){
		return (mask & flags) > 0;
	}
	
	/**
	 * Gets the current permissions mask
	 */
	public int getMask(){
		return this.mask;
	}
}
