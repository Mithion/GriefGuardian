package com.mithion.griefguardian.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonGuiHandler implements IGuiHandler {
	public static final byte REMOTE_INVENTORY = 1;
	public static final byte CLAIM_MANAGEMENT = 2;
	public static final byte NBT_EDITOR = 3;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return getServerGuiElement(ID, player, world, x, y, z);
	}
}
