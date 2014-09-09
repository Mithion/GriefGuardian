package com.mithion.griefguardian.config;

import java.io.File;
import java.util.HashMap;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.mithion.griefguardian.util.Convert;

public class Config extends Configuration{
	
	private HashMap<String, ConfigEntry> configEntries;	
	private static final String CATEGORY_MYSQL = "MYSQL";
	private static final String CATEGORY_CHAT = "CHAT";
	
	public Config(File file){
		super(file);
		
		if (configEntries == null){
			configEntries = new HashMap<String, Config.ConfigEntry>();
		}
		this.addEntry(ConfigKeys.mysql_db_host, CATEGORY_MYSQL, "localhost", String.class, "This is where your mysql server is.");
		this.addEntry(ConfigKeys.mysql_db_port, CATEGORY_MYSQL, 3306, Integer.class, "This is the port your instance of MySQL is listening on");
		this.addEntry(ConfigKeys.mysql_db_name, CATEGORY_MYSQL, "minecraft", String.class, "This is the name of the database that GriefGuardian should use");
		this.addEntry(ConfigKeys.mysql_db_user, CATEGORY_MYSQL, "mcsvc", String.class, "This is the username to allow GriefGuardian to use the database");
		this.addEntry(ConfigKeys.mysql_db_pass, CATEGORY_MYSQL, "admin", String.class, "This is the password to allow GriefGuardian to use the database");
		
		this.addEntry(ConfigKeys.spamguard_time, CATEGORY_CHAT, 750, Integer.class, "How long must pass between user chats to not be considered spam (in ms)?");
		this.addEntry(ConfigKeys.spamguard_silent, CATEGORY_CHAT, true, Boolean.class, "Should a message be sent to a player if their chat is blocked?");
		
		save();
	}
	
	private <T> void addEntry(String key, String category, T _default, Class type){
		configEntries.put(key, new ConfigEntry<T>(key, category, _default, type));
	}
	
	private <T> void addEntry(String key, String category, T _default, Class type, String comment){
		configEntries.put(key, new ConfigEntry<T>(key, category, _default, type));
	}
	
	public String getString(String key){
		ConfigEntry<String> entry = configEntries.get(key);
		return entry.fetch();				
	}
	
	public int getInt(String key){
		ConfigEntry<Integer> entry = configEntries.get(key);
		return entry.fetch();
	}
	
	public boolean getBoolean(String key){
		ConfigEntry<Boolean> entry = configEntries.get(key);
		return entry.fetch();
	}
	
	public double getDouble(String key){
		ConfigEntry<Double> entry = configEntries.get(key);
		return entry.fetch();
	}
	
	public float getFloat(String key){
		ConfigEntry<Float> entry = configEntries.get(key);
		return entry.fetch();
	}
	
	private class ConfigEntry<T>{
		private final String KEY;
		private final String CATEGORY;
		
		private boolean cache = true;
		private T value;
		private T _default;
		
		private Class persistentClass;
		
		public ConfigEntry(String key, String category, T _default, Class type, boolean cache, String comment){
			this.KEY = key;
			this.CATEGORY = category;
			this._default = _default;
			this.cache = cache;
			
			this.persistentClass = type;
			
			if (cache){
				Property p = get(CATEGORY, KEY, _default.toString(), comment);
				if (comment != null)
					p = get(CATEGORY, KEY, _default.toString(), comment);
				else
					p = get(CATEGORY, KEY, _default.toString());
				value = (T) Convert.toVariant(p.getString(), persistentClass);
			}
		}
		
		public ConfigEntry(String key, String category, T _default, Class type, boolean cache){
			this(key, category, _default, type, cache, null);
		}
		
		public ConfigEntry(String key, String category, T _default, Class type){
			this(key, category, _default, type, true);
		}
		
		public void store(T newValue){
			if (cache)
				value = newValue;
			get(CATEGORY, KEY, _default.toString()).set(newValue.toString());
			save();
		}
		
		public T fetch(){
			if (cache)
				return value;
			return (T) Convert.toVariant(get(CATEGORY, KEY, _default.toString()).getString(), persistentClass);
		}
	}
}
