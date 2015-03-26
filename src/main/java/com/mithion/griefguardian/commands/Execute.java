package com.mithion.griefguardian.commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

public class Execute extends CommandBase{

	@Override
	public String getCommandName() {
		return "execute";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/execute <class|CREATURES|MONSTERS|ALL> [radius]";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {		
		int radius;
		try {
			radius = args.length >= 2 ? parseInt(args[1], 5) : -1;
		} catch (NumberInvalidException e2) {
			e2.printStackTrace();
			return;
		}
		
		String target = args[0].toUpperCase().trim();
		
		Class<?> targetClazz = target.equals("CREATURES") ? EntityCreature.class : 
							target.equals("MONSTERS") ? EntityMob.class : 
							target.equals("ALL") ? EntityLiving.class : 
							target.equals("AMBIENT") ? EntityAmbientCreature.class : 
							null;
		
		List<Entity> entities;
		try {
			entities = radius < 0 ? 
					commandSender.getEntityWorld().loadedEntityList : 
					commandSender.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, getCommandSenderAsPlayer(commandSender).getBoundingBox().expand(radius, radius, radius));
		} catch (PlayerNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
			
		int count = 0;
		for (Entity e : entities){
			if (e instanceof EntityPlayer) //never kill players!
				continue;
			if ( (targetClazz == null && e.getClass().getName().endsWith(args[0].trim())) || (targetClazz != null && targetClazz.isAssignableFrom(e.getClass())) ){
				e.setDead();
				count++;
			}
		}
		
		commandSender.addChatMessage(new ChatComponentTranslation("griefguardian.commands.execute", count));
	}

}
