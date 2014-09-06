package com.mithion.griefguardian.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.List;

import com.mithion.griefguardian.claims.ClaimsList;
import com.mithion.griefguardian.claims.ClaimsList.ActionResults;
import com.mithion.griefguardian.claims.PermissionsMutex;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;

public class ModifyACL extends CommandBase{

	@Override
	public String getCommandName() {
		return "modifyacl";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "/modifyacl ADD|REMOVE|CLEAR <player_name|team_name|EVERYONE> FLAG1 FLAG2 FLAG3 ...";
	}

	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		if ((args.length == 2 && args[0].toUpperCase().trim() != "CLEAR") || args.length < 3){
			throw new WrongUsageException(getCommandUsage(commandSender), new Object[0]);
		}
		if (args.length == 2){
			ChunkCoordinates coords = commandSender.getPlayerCoordinates();
			ActionResults res = ClaimsList.For(commandSender.getEntityWorld()).clearClaimPermissions(commandSender, args[1].trim(), coords.posX, coords.posY, coords.posZ);
			commandSender.addChatMessage(new ChatComponentText(res.message));
		}else{
			ChunkCoordinates coords = commandSender.getPlayerCoordinates();
			int mask = buildFlagsFromArgs(stripArray(args, 2));
			if (args[0].toUpperCase().trim().equals("ADD")){
				ActionResults res = ClaimsList.For(commandSender.getEntityWorld()).addClaimPermissions(commandSender, args[1].trim(), mask, coords.posX, coords.posY, coords.posZ);
				commandSender.addChatMessage(new ChatComponentText(res.message));
			}else if (args[0].toUpperCase().trim().equals("REMOVE")){
				ActionResults res = ClaimsList.For(commandSender.getEntityWorld()).removeClaimPermissions(commandSender, args[1].trim(), mask, coords.posX, coords.posY, coords.posZ);
				commandSender.addChatMessage(new ChatComponentText(res.message));
			}else{
				throw new WrongUsageException(getCommandUsage(commandSender), new Object[0]);
			}
		}
	}
	
	private String[] stripArray(String[] arr, int num){		
		if (num > arr.length - 1)
			return arr;
		
		String[] newArr = new String[arr.length - num];
		
		for (int i = 0 ; i < arr.length; ++i){
			if (i < num)
				continue;
			newArr[i-num] = arr[i];
		}
		return newArr;
	}

	private int buildFlagsFromArgs(String[] args){
		Field[] fields = PermissionsMutex.class.getDeclaredFields();
		int mask = 0;
		for (Field f : fields){
			if (f.getType() == int.class && (f.getModifiers() & Modifier.STATIC) == Modifier.STATIC && (f.getModifiers() & Modifier.FINAL) == Modifier.FINAL){				
				for (String s : args){
					if (s.toUpperCase().trim().equals(f.getName().toUpperCase())){
						try{
							f.setAccessible(true);
							mask |= f.getInt(null);
							f.setAccessible(false);
						}catch(Throwable t){
							t.printStackTrace();
						}
					}
				}
			}
		}
		
		return mask;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender commandSender, String[] currentArgs) {
		ArrayList<String> choices = new ArrayList<String>();
		if (currentArgs.length == 1){ //need Add|REMOVE|CLEAR
			return getListOfStringsMatchingLastWord(currentArgs, "ADD", "REMOVE", "CLEAR");
		}else if (currentArgs.length == 2){ //need target
			choices.add("EVERYONE");
			for (Object o : commandSender.getEntityWorld().playerEntities){
				EntityPlayer p = (EntityPlayer)o;
				choices.add(p.getCommandSenderName());
			}
			for (Object o : commandSender.getEntityWorld().getScoreboard().getTeamNames()){
				String s = (String)o;
				choices.add(s);
			}
			return getListOfStringsMatchingLastWord(currentArgs, choices.toArray(new String[choices.size()]));
		}else{
			Field[] fields = PermissionsMutex.class.getDeclaredFields();
			for (Field f : fields){
				if (f.isAccessible() && f.getDeclaringClass() == int.class && f.getModifiers() == (Modifier.STATIC | Modifier.FINAL)){
					choices.add(f.getName());
				}
			}
			return getListOfStringsMatchingLastWord(currentArgs, choices.toArray(new String[choices.size()]));
		}
	}

}
