package utils;

import java.util.HashMap;

public class ProcessSynchronizer {
	private static HashMap<Integer, Boolean> mutex = new HashMap<Integer, Boolean>();
	public ProcessSynchronizer(){
	
	}
	
	public static boolean isInCritical(int processID){
		return false;
	}
}
