package com.mithion.griefguardian.util;


public class AdminUtils {
	
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_TEMPBANNED = 2;
	public static final int STATUS_PERMABANNED = 3;
	public static final int STATUS_TEMPBANNED_IP = 4;
	public static final int STATUS_PERMABANNED_IP = 5;
	
	public static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
}
