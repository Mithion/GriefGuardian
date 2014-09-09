package com.mithion.griefguardian.util;

import net.minecraft.nbt.NBTTagCompound;

public class WarpPoint{
	public final int dimension;
	public final double x;
	public final double y;
	public final double z;

	public WarpPoint(double x, double y, double z, int dim){
		this.dimension = dim;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static WarpPoint FromNBT(NBTTagCompound compound){
		double x = compound.getDouble("wp_x");
		double y = compound.getDouble("wp_y");
		double z = compound.getDouble("wp_z");
		int d = compound.getInteger("wp_dim");
		
		return new WarpPoint(x, y, z, d);
	}
	
	public static NBTTagCompound ToNBT(WarpPoint point){
		NBTTagCompound compound = new NBTTagCompound();
		compound.setDouble("wp_x", point.x);
		compound.setDouble("wp_y", point.y);
		compound.setDouble("wp_z", point.z);
		compound.setDouble("wp_dim", point.dimension);
		
		return compound;
	}
}