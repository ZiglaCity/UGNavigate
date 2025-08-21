package com.ugnavigate.utils;

public class ErrorHandler {

	public static String friendlyMessage(Throwable t) {
		if (t == null) return "Unknown error";
		return t.getClass().getSimpleName() + ": " + t.getMessage();
	}
}
