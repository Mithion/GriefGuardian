package com.mithion.griefguardian.util;

import java.lang.reflect.ParameterizedType;

import scala.NotImplementedError;

public class Convert {
	public static String toString(Object value){
		if (value == null)
			return "";
		return value.toString();
	}
	
	public static int toInt(Object value){
		if (value == null)
			return 0;
		if (value instanceof Integer){
			return (Integer)value;
		}
		try{
			return Integer.parseInt(value.toString());
		}catch(NumberFormatException nex){
			return 0;
		}catch(Throwable t){
			return 0;
		}
	}
	
	public static float toFloat(Object value){
		if (value == null)
			return 0f;
		if (value instanceof Float){
			return (Float)value;
		}
		try{
			return Float.parseFloat(value.toString());
		}catch(NumberFormatException nex){
			return 0;
		}catch(Throwable t){
			return 0;
		}
	}
	
	public static double toDouble(Object value){
		if (value == null)
			return 0.0;
		if (value instanceof Double){
			return (Double)value;
		}
		try{
			return Double.parseDouble(value.toString());
		}catch(NumberFormatException nex){
			return 0;
		}catch(Throwable t){
			return 0;
		}
	}
	
	public static boolean toBoolean(Object value){
		if (value == null)
			return false;
		if (value instanceof Boolean){
			return (Boolean)value;
		}
		try{
			return Boolean.parseBoolean(value.toString());
		}catch(NumberFormatException nex){
			return false;
		}catch(Throwable t){
			return false;
		}
	}

	public static Object toVariant(Object value, Class desiredCast){
		if (desiredCast == Integer.class || desiredCast == int.class){
			return toInt(value);
		}else if (desiredCast == Float.class || desiredCast == float.class){
			return toFloat(value);
		}else if (desiredCast == Double.class || desiredCast == double.class){
			return toDouble(value);
		}else if (desiredCast == String.class){
			return toString(value);
		}else if (desiredCast == Boolean.class || desiredCast == boolean.class){
			return toBoolean(value);
		}
		
		throw new NotImplementedError("GG Convert >> The specified cast is not known.");
	}
}
