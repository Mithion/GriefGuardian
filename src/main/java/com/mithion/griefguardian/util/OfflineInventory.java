package com.mithion.griefguardian.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.IChatComponent;

import com.mithion.griefguardian.gui.ContainerRemoteInventory;

public class OfflineInventory implements IInventory {

	private ItemStack[] inventory;
	private int inventorySize = 1;
	private String userName = "";

	public OfflineInventory(NBTTagCompound remoteInventoryCompound, String userName){
		this.userName = userName;
		
		NBTTagList tagList = remoteInventoryCompound.getTagList("Inventory", 10);

		ItemStack[] mainInventory = new ItemStack[36];
		ItemStack[] armorInventory = new ItemStack[4];

		for (int i = 0; i < tagList.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = tagList.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

			if (itemstack != null)
			{
				if (j >= 0 && j < mainInventory.length)
				{
					mainInventory[j] = itemstack;
				}

				if (j >= 100 && j < armorInventory.length + 100)
				{
					armorInventory[j - 100] = itemstack;
				}
			}
		}
		
		mergeInventories(mainInventory, armorInventory);
	}
	
	private void mergeInventories(ItemStack[] inv1, ItemStack[] inv2){
		this.inventory = new ItemStack[inv1.length + inv2.length];
		this.inventorySize = this.inventory.length;
		
		int index = 0;
		for (int i = 0; i < inv1.length; ++i){
			inventory[index++] = inv1[i] != null ? inv1[i].copy() : null;
		}
		for (int i = 0; i < inv2.length; ++i){
			inventory[index++] = inv2[i] != null ? inv2[i].copy() : null;
		}
	}

	public void writeToNBTTagCompound(NBTTagCompound compound){
		NBTTagList tagList = new NBTTagList();

		int i;
		NBTTagCompound nbttagcompound;

		for (i = 0; i < 36; ++i)
		{
			if (this.inventory[i] != null)
			{
				nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				this.inventory[i].writeToNBT(nbttagcompound);
				tagList.appendTag(nbttagcompound);
			}
		}

		for (i = 0; i < 4; ++i)
		{
			if (this.inventory[36+i] != null)
			{
				nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)(i + 100));
				this.inventory[36+i].writeToNBT(nbttagcompound);
				tagList.appendTag(nbttagcompound);
			}
		}
		
		compound.setTag("Inventory", tagList);
	}

	@Override
	public int getSizeInventory() {
		return inventorySize;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		if (var1 >= inventory.length){
			return null;
		}
		return inventory[var1];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if(inventory[i] != null)
		{
			if(inventory[i].stackSize <= j)
			{
				ItemStack itemstack = inventory[i];
				inventory[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = inventory[i].splitStack(j);
			if(inventory[i].stackSize == 0)
			{
				inventory[i] = null;
			}
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (inventory[i] != null)
		{
			ItemStack itemstack = inventory[i];
			inventory[i] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory[i] = itemstack;
		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}

	public static void open(EntityPlayerMP player, OfflineInventory target, String offlineUserName){
		if (player.openContainer != player.inventoryContainer)
        {
			player.closeScreen();
        }

		player.getNextWindowId();
		player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow());
		player.openContainer = new ContainerRemoteInventory(player.inventory, target, player);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
	}

	@Override
	public String getName() {
		return userName;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public IChatComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openInventory(EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
}
