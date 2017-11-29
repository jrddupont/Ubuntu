package utils;

import java.util.ArrayList;

public class ProcessSynchronizer {
	private static ArrayList<Integer> locks = new ArrayList<Integer>();
	
	public static boolean lock(int resource){
		if(locks.contains(resource)){
			return false;
		}
		locks.add(resource);
		return true;
	}
	public static void signal(int i){
		locks.remove(i);
	}
}
