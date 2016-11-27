package com.qbanalytix.cognostest.common;

public class ElapsedTime {

	public static String getElapsedTimeString(long startTime) {

		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;

		int seconds = (int) (elapsedTime / 1000);
		int millis = (int) (elapsedTime % 1000);

		int minutes = seconds / 60;
		seconds %= 60;

		int hours = minutes / 60;
		minutes %= 60;

		return new StringBuilder().append(hours).append(":").append(minutes).append(":").append(seconds + ",")
				.append(millis).toString();
	}
}
