package com.mithion.griefguardian.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

import com.mithion.griefguardian.util.OfflineInventory;
import com.mithion.griefguardian.util.PlayerDataUtils;

public class ContainerRemoteInventory extends ContainerChest {

	private final String userName;
	private final OfflineInventory remoteInventory;
	
	public ContainerRemoteInventory(IInventory p_i1806_1_, OfflineInventory p_i1806_2_, String userName) {
		super(p_i1806_1_, p_i1806_2_);
		
		this.userName = userName;
		this.remoteInventory = p_i1806_2_;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer p_75134_1_) {
	
		NBTTagCompound offlineData = PlayerDataUtils.loadOfflinePlayerData(userName);
		remoteInventory.writeToNBTTagCompound(offlineData);
		PlayerDataUtils.SaveOfflinePlayerData(userName, offlineData);
		
		super.onContainerClosed(p_75134_1_);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}
}
