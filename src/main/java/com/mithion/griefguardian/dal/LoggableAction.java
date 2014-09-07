package com.mithion.griefguardian.dal;

import com.mithion.griefguardian.api.ILoggableAction;

public class LoggableAction implements ILoggableAction {

	private final String desc;
	private final int id;
	
	public LoggableAction(int id, String desc){ 
		this.id = id;
		this.desc = desc;
	}
	
	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public int getID() {
		return id;
	}

}
