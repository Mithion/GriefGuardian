package com.mithion.griefguardian.dal;

import net.minecraft.item.ItemStack;

public class LogEntry {
	public final String userName;
	public final String ipAddr;
	public final int action;
	public final int dimension;
	public final String dimensionName;
	public final int x;
	public final int y;
	public final int z;
	public final String itemIdentifier;
	public final int itemMeta;
	public final String description;
	
	public LogEntry(String userName, String ipAddr, int action, int dimID, String dimName, int x, int y, int z, ItemStack stack, String desc, Object...params){
		this.userName = userName;
		this.ipAddr = ipAddr;
		this.action = action;
		this.dimension = dimID;
		this.dimensionName = dimName;
		this.x = x;
		this.y = y;
		this.z = z;
		this.itemIdentifier = stack != null ? stack.getUnlocalizedName() : null;
		this.itemMeta = stack != null ? stack.getItemDamage() : -1;
		this.description = String.format(desc, params);
	}
}
