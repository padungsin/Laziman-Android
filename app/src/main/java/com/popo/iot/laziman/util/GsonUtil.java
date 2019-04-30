package com.popo.iot.laziman.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {
	public static String toJson(Object src){
		Gson gson = new GsonBuilder()
			     .enableComplexMapKeySerialization()
			     .setDateFormat("yyyy-MM-dd HH:mm:ss")
			     .setPrettyPrinting()
			     .setVersion(1.0)
			     .create();
		
		
		return gson.toJson(src);
	}
}
