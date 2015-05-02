package com.mithion.griefguardian.eventhandlers;

import java.util.HashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.mithion.griefguardian.GriefGuardian;
import com.mithion.griefguardian.config.ConfigKeys;

/*
 * Contains all the 'entry point' event handlers for Forge events.
 * The only events handled here pertain to spam blocking.
 * 
 * No actual processing should be done here.  It should all be delegated out to handlers.
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
public class SpamGuardEventHandler {
	
	private final HashMap<String, Long> chatSpamCooldown;
	
	public SpamGuardEventHandler(){
		chatSpamCooldown = new HashMap<String, Long>();
	}
	
	@SubscribeEvent
	public void onServerChat(ServerChatEvent event){
		long curTime = MinecraftServer.getCurrentTimeMillis();
		if (chatSpamCooldown.containsKey(event.player.getCommandSenderEntity())){
			if (curTime - chatSpamCooldown.get(event.player.getCommandSenderEntity()) < GriefGuardian.config.getInt(ConfigKeys.spamguard_time)){
				if (!GriefGuardian.config.getBoolean(ConfigKeys.spamguard_silent))
					event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Your message was too soon after the previous one and was not sent."));
				event.setCanceled(true);
			}			
		}
		chatSpamCooldown.put(event.player.getCommandSenderEntity().getName(), curTime);		
	}
}
