package com.mithion.griefguardian.dal;

import java.util.HashMap;

import net.minecraftforge.fml.common.FMLLog;

import com.mithion.griefguardian.GriefGuardian;

public class LoggableActionManager {
	private final HashMap<Integer, LoggableAction> loggableActions;
	
	public static final LoggableActionManager instance = new LoggableActionManager();
	private LoggableActionManager(){
		loggableActions = new HashMap<Integer, LoggableAction>();
	}
	
	public void init(){
		loggableActions.clear();
		loggableActions.putAll(GriefGuardian._dal.getLoggableActions());
		FMLLog.info("GG >> Loaded %d loggable actions.", loggableActions.size());
	}
	
	public int getIDForAction(String desc){
		for(Integer i : loggableActions.keySet()){
			LoggableAction action = loggableActions.get(i);
			if (action.getDescription().equals(desc))
				return action.getID();
		}
		return -1;
	}
}
