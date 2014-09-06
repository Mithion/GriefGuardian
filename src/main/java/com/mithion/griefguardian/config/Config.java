package com.mithion.griefguardian.config;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

import com.google.common.reflect.TypeResolver;
import com.mithion.griefguardian.util.Convert;

import net.minecraftforge.common.config.Configuration;

public class Config extends Configuration{
	
	private HashMap<String, ConfigEntry> configEntries;	
	private static final String CATEGORY_MYSQL = "MYSQL";
	
	public Config(File file){
		super(file);
		
		if (configEntries == null){
			configEntries = new HashMap<String, Config.ConfigEntry>();
		}
		this.addEntry(ConfigKeys.mysql_db_host, CATEGORY_MYSQL, "localhost", String.class);
		this.addEntry(ConfigKeys.mysql_db_port, CATEGORY_MYSQL, 3306, Integer.class);
		this.addEntry(ConfigKeys.mysql_db_name, CATEGORY_MYSQL, "minecraft", String.class);
		this.addEntry(ConfigKeys.mysql_db_user, CATEGORY_MYSQL, "mcsvc", String.class);
		this.addEntry(ConfigKeys.mysql_db_pass, CATEGORY_MYSQL, "admin", String.class);
		
		save();
	}
	
	private <T> void addEntry(String key, String category, T _default, Class type){
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
		
		public ConfigEntry(String key, String category, T _default, Class type, boolean cache){
			this.KEY = key;
			this.CATEGORY = category;
			this._default = _default;
			this.cache = cache;
			
			this.persistentClass = type;
			
			if (cache){
				value = (T) Convert.toVariant(get(CATEGORY, KEY, _default.toString()).getString(), persistentClass);
			}
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
