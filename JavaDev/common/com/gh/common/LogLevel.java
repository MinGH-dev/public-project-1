package com.gh.common;

public enum LogLevel {
	DEBUG(0),
	INFO(1),
	ERROR(2),
	SYSTEM(3)
	;
	
	private int level = 0;
	private static int currentLevel = 0;
	private LogLevel (int level) {
		this.level = level;
	}
	
	public int getLevel () {
		return this.level;
	}
	
	public static void setLevel (LogLevel level) throws Exception {
		currentLevel = LogLevel.valueOf(level.toString()).getLevel();
	}
	
	public static void setLevel (int level) throws Exception {
		currentLevel = level;
	}
	
	public static LogLevel getLogLevel () {
		LogLevel logLevel = DEBUG;
		
		for (LogLevel currLevel : LogLevel.values()) {
			if (currLevel.level == currentLevel) {
				logLevel = currLevel;
				break;
			}
		}
		
		return logLevel;
	}
	
	public boolean isMoreThanLevel () {
		if (level >= currentLevel) {
			return true;
		} else {
			return false;
		}
	}
}
