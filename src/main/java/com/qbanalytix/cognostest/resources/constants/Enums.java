package com.qbanalytix.cognostest.resources.constants;

public class Enums {

	public static enum ResponseStatus {
		SUCCESS, FAILED;

		public static ResponseStatus fromString(String string) {
			if (string.equalsIgnoreCase("SUCCESS")) {
				return SUCCESS;
			} else if (string.equalsIgnoreCase("FAILED")) {
				return FAILED;
			} else {
				throw new RuntimeException("Cannot get value of '" + string + "' for ResponseStatus");
			}
		}
	}
}
