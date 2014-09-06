package com.mithion.griefguardian.eventhandlers;

import net.minecraftforge.event.ServerChatEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
	@SubscribeEvent
	public void onServerChat(ServerChatEvent event){
		
	}
}
