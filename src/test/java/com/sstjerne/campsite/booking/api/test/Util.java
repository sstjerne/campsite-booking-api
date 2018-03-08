package com.sstjerne.campsite.booking.api.test;

public class Util {

	public static String getResourceIdFromUrl(String locationUrl) {
		String[] parts = locationUrl.split("/");
		return parts[parts.length - 1];
	}
	

}
