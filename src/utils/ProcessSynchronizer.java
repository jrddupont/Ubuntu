package utils;

import java.util.ArrayList;

public class ProcessSynchronizer {
	private static ArrayList<Integer> locks = new ArrayList<Integer>();
	
	public static boolean lock(Integer resource){
		for(int i : locks){
			System.out.print(i + " ");
		}
		System.out.println();
		if(locks.contains(resource)){
			return false;
		}
		locks.add(resource);
		return true;
	}
	public static void signal(Integer i){
		locks.remove(i);
	}
}
