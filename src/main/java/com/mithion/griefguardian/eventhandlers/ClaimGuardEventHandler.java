package com.mithion.griefguardian.eventhandlers;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.api.Actions;
import com.mithion.griefguardian.claims.ClaimsList;
import com.mithion.griefguardian.claims.PermissionsMutex;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/*
 * Contains all the 'entry point' event handlers for Forge events.
 * The only events handled here pertain to protecting player claims.
 * 
 * No actual processing should be done here.  It should all be delegated out to handlers.
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
public class ClaimGuardEventHandler {
	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event){
		if (event.entityPlayer.worldObj.isRemote)
			return;

		if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.ENTITY_INTERACT, event.target.posX, event.target.posY, event.target.posZ))
			event.setCanceled(true);
		else
			GriefGuardian._dal.logAction(
					(EntityPlayerMP)event.entityPlayer, 
					Actions.ENTITY_INTERACT, 
					(int)Math.floor(event.entity.posX), 
					(int)Math.floor(event.entity.posY), 
					(int)Math.floor(event.entity.posZ), 
					event.entityPlayer.getCurrentEquippedItem(), 
					"");
	}

	@SubscribeEvent
	public void onEntityAttacked(AttackEntityEvent event){
		if (event.entityPlayer.worldObj.isRemote)
			return;

		if (event.target instanceof EntityPlayer)
			if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.HARM_PLAYERS, event.target.posX, event.target.posY, event.target.posZ))
				event.setCanceled(true);

		if (event.target instanceof EntityCreature)
			if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.HARM_CREATURES, event.target.posX, event.target.posY, event.target.posZ))
				event.setCanceled(true);

		if (event.target instanceof EntityMob)
			if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.HARM_MONSTERS, event.target.posX, event.target.posY, event.target.posZ))
				event.setCanceled(true);		
	}

	@SubscribeEvent
	public void onEntityHurt(LivingHurtEvent event){
		if (event.entity.worldObj.isRemote)
			return;

		EntityPlayer player = null;
		if (event.source.getSourceOfDamage() != null && event.source.getSourceOfDamage() instanceof EntityPlayer){
			player = (EntityPlayer)event.source.getSourceOfDamage();
		}
		if (player == null)
			return;

		if (event.entity instanceof EntityPlayer)
			if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(player, PermissionsMutex.HARM_PLAYERS, event.entity.posX, event.entity.posY, event.entity.posZ))
				event.setCanceled(true);

		if (event.entity instanceof EntityCreature)
			if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(player, PermissionsMutex.HARM_CREATURES, event.entity.posX, event.entity.posY, event.entity.posZ))
				event.setCanceled(true);

		if (event.entity instanceof EntityMob)
			if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(player, PermissionsMutex.HARM_MONSTERS, event.entity.posX, event.entity.posY, event.entity.posZ))
				event.setCanceled(true);
	}

	@SubscribeEvent
	public void onBonemeal(BonemealEvent event){
		if (event.entityPlayer.worldObj.isRemote)
			return;

		if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.USE_BONEMEAL, event.x, event.y, event.z))
			event.setCanceled(true);
		else
			GriefGuardian._dal.logAction(
					(EntityPlayerMP)event.entityPlayer, 
					Actions.ITEM_USE, 
					(int)Math.floor(event.entity.posX), 
					(int)Math.floor(event.entity.posY), 
					(int)Math.floor(event.entity.posZ), 
					event.entityPlayer.getCurrentEquippedItem(), 
					"");
	}

	@SubscribeEvent
	public void onItemPickupAttempt(EntityItemPickupEvent event){
		if (event.entityPlayer.worldObj.isRemote)
			return;

		if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.PICKUP_ITEMS, event.item.posX, event.item.posY, event.item.posZ))
			event.setCanceled(true);
		else
			GriefGuardian._dal.logAction(
					(EntityPlayerMP)event.entityPlayer, 
					Actions.ITEM_PICKUP, 
					(int)Math.floor(event.entity.posX), 
					(int)Math.floor(event.entity.posY), 
					(int)Math.floor(event.entity.posZ), 
					event.item.getEntityItem(), 
					"");
	}

	@SubscribeEvent
	public void onBucketFillAttempt(FillBucketEvent event){
		if (event.entityPlayer.worldObj.isRemote)
			return;

		if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.USE_ITEMS, event.target.blockX, event.target.blockY, event.target.blockZ))
			event.setCanceled(true);
		else
			GriefGuardian._dal.logAction(
					(EntityPlayerMP)event.entityPlayer, 
					Actions.ITEM_USE, 
					(int)Math.floor(event.entity.posX), 
					(int)Math.floor(event.entity.posY), 
					(int)Math.floor(event.entity.posZ), 
					event.entityPlayer.getCurrentEquippedItem(), 
					"");
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event){
		if (event.entityPlayer.worldObj.isRemote)
			return;

		switch (event.action){
		case LEFT_CLICK_BLOCK:
			break;
		case RIGHT_CLICK_AIR:
			if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.USE_ITEMS, event.x, event.y, event.z))
				event.setCanceled(true);
			else
				GriefGuardian._dal.logAction(
						(EntityPlayerMP)event.entityPlayer, 
						Actions.ITEM_USE, 
						(int)Math.floor(event.entity.posX), 
						(int)Math.floor(event.entity.posY), 
						(int)Math.floor(event.entity.posZ), 
						event.entityPlayer.getCurrentEquippedItem(), 
						"");
			break;
		case RIGHT_CLICK_BLOCK:
			TileEntity te = event.entity.worldObj.getTileEntity(event.x, event.y, event.z);
			if (te != null && !event.entityPlayer.isSneaking()){
				if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.OPEN_CONTAINERS, event.x, event.y, event.z))
					event.setCanceled(true);
				else
					GriefGuardian._dal.logAction(
							(EntityPlayerMP)event.entityPlayer, 
							Actions.BLOCK_USE, 
							(int)Math.floor(event.entity.posX), 
							(int)Math.floor(event.entity.posY), 
							(int)Math.floor(event.entity.posZ), 
							new ItemStack(event.world.getBlock(event.x, event.y, event.z), 1, event.world.getBlockMetadata(event.x, event.y, event.z)), 
							"");
			}else{
				ItemStack currentItem = event.entityPlayer.getCurrentEquippedItem();
				if (currentItem != null && currentItem.getItem() instanceof ItemBlock){
					int x = event.x;
					int y = event.y;
					int z = event.z;

					switch(event.face){
					case 0:
						y--;
						break;
					case 1:
						y++;
						break;
					case 2:
						z--;
						break;
					case 3:
						z++;
						break;
					case 4:
						x--;
						break;
					case 5:
						x++;
						break;
					}

					if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.PLACE_BLOCKS, x, y, z))
						event.setCanceled(true);
					else
						GriefGuardian._dal.logAction(
								(EntityPlayerMP)event.entityPlayer, 
								Actions.BLOCK_PLACE, 
								(int)Math.floor(event.x), 
								(int)Math.floor(event.y), 
								(int)Math.floor(event.z), 
								event.entityPlayer.getCurrentEquippedItem(), 
								"");
				}else if (currentItem != null){
					if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.USE_ITEMS, event.x, event.y, event.z))
						event.setCanceled(true);
					else
						GriefGuardian._dal.logAction(
								(EntityPlayerMP)event.entityPlayer, 
								Actions.ITEM_USE, 
								(int)Math.floor(event.x), 
								(int)Math.floor(event.y), 
								(int)Math.floor(event.z), 
								event.entityPlayer.getCurrentEquippedItem(), 
								"");
				}
			}
			break;
		default:
			break;		
		}
	}

	@SubscribeEvent
	public void onPlayerAttemptXPPickup(PlayerPickupXpEvent event){
		if (event.entityPlayer.worldObj.isRemote)
			return;

		if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.entityPlayer, PermissionsMutex.PICKUP_XP, event.orb.posX, event.orb.posY, event.orb.posZ))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onItemDrop(ItemTossEvent event){
		if (event.entity.worldObj.isRemote)
			return;

		if (!ClaimsList.For(event.entity.worldObj).actionIsTrusted(event.player, PermissionsMutex.DROP_ITEMS, event.entityItem.posX, event.entityItem.posY, event.entityItem.posZ))
			event.setCanceled(true);
		else
			GriefGuardian._dal.logAction(
					(EntityPlayerMP)event.player, 
					Actions.ITEM_DROP, 
					(int)Math.floor(event.entity.posX), 
					(int)Math.floor(event.entity.posY), 
					(int)Math.floor(event.entity.posZ), 
					event.player.getCurrentEquippedItem(), 
					"");
	}

	@SubscribeEvent
	public void onPlayerTryBreakBlock(BlockEvent.BreakEvent event){
		if (event.getPlayer().worldObj.isRemote)
			return;

		if (!ClaimsList.For(event.getPlayer().worldObj).actionIsTrusted(event.getPlayer(), PermissionsMutex.BREAK_BLOCKS, event.x, event.y, event.z))
			event.setCanceled(true);
		else
			GriefGuardian._dal.logAction(
					(EntityPlayerMP)event.getPlayer(), 
					Actions.BLOCK_BREAK, 
					(int)Math.floor(event.x), 
					(int)Math.floor(event.y), 
					(int)Math.floor(event.z), 
					event.getPlayer().getCurrentEquippedItem(), 
					"");
	}

	//TODO:  Fire spread, liquid movement, entity stacking, mob griefing
}
