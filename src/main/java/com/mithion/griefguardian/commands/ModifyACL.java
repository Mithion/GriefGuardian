package com.mithion.griefguardian.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import com.mithion.griefguardian.claims.ClaimManager;
import com.mithion.griefguardian.claims.ClaimsList;
import com.mithion.griefguardian.claims.ClaimsList.ActionResults;
import com.mithion.griefguardian.claims.PermissionsMutex;

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
	public void processCommand(ICommandSender commandSender, String[] args) throws WrongUsageException {
		if ((args.length == 2 && args[0].toUpperCase().trim() != "CLEAR") || args.length < 3){
			throw new WrongUsageException(getCommandUsage(commandSender), new Object[0]);
		}
		if (args.length == 2){
			BlockPos coords = commandSender.getPosition();
			ActionResults res = ClaimsList.For(commandSender.getEntityWorld()).clearClaimPermissions(commandSender, args[1].trim(), coords.getX(), coords.getY(), coords.getZ());
			commandSender.addChatMessage(new ChatComponentText(res.message));
		}else{
			BlockPos coords = commandSender.getPosition();
			int mask = buildFlagsFromArgs(stripArray(args, 2));
			
			Team team;
			try {
				team = getCommandSenderAsPlayer(commandSender).getWorldScoreboard().getTeam(args[1].trim());
			} catch (PlayerNotFoundException e) {
				e.printStackTrace();
				return;
			}
			String identifier = team == null ? args[1].trim() : ClaimManager.instance.createTeamIdentifier(team);
			
			if (args[0].toUpperCase().trim().equals("ADD")){				
				ActionResults res = ClaimsList.For(commandSender.getEntityWorld()).addClaimPermissions(commandSender, identifier, mask, coords.getX(), coords.getY(), coords.getZ());
				commandSender.addChatMessage(new ChatComponentText(res.message));
			}else if (args[0].toUpperCase().trim().equals("REMOVE")){
				ActionResults res = ClaimsList.For(commandSender.getEntityWorld()).removeClaimPermissions(commandSender, identifier, mask, coords.getX(), coords.getY(), coords.getZ());
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
	
	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender commandSender, String[] currentArgs, BlockPos pos) {
		ArrayList<String> choices = new ArrayList<String>();
		if (currentArgs.length == 1){ //need Add|REMOVE|CLEAR
			return getListOfStringsMatchingLastWord(currentArgs, "ADD", "REMOVE", "CLEAR");
		}else if (currentArgs.length == 2){ //need target
			choices.add("EVERYONE");
			for (Object o : commandSender.getEntityWorld().playerEntities){
				EntityPlayer p = (EntityPlayer)o;
				choices.add(p.getCommandSenderEntity().getName());
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
