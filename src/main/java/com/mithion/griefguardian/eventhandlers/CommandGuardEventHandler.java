package com.mithion.griefguardian.eventhandlers;

import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/*
 * Contains all the 'entry point' event handlers for Forge events.
 * The only events handled here pertain to controlling command permissions.
 * 
 * No actual processing should be done here.  It should all be delegated out to handlers.
 * 
 * Author: Mithion
 * Sept 6, 2014
 * 
 */
public class CommandGuardEventHandler {
	@SubscribeEvent
	public void onCommandIssued(CommandEvent event){
		
	}
}
