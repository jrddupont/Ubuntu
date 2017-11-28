package utils;
import java.util.ArrayList;
import java.util.Arrays;

import bursts.Burst;


public class Process {
	public static final int IO = 0;
	public static final int CPU = 1;
	public static final int CS = 2;
	public int id;
	public int memory;
	private int runtime = 0;
	private int estimatedTotalRuntime = 0;
	ArrayList<Burst> bursts = new ArrayList<Burst>();
	private int[] processState; 
	Process( int id, int memory, ArrayList<Burst> newBursts ){
		this.id = id;
		this.memory = memory;
		bursts.addAll(newBursts);
		
		for(Burst b : bursts){
			estimatedTotalRuntime += b.getEstimatedTime();
		}
		processState = new int[estimatedTotalRuntime];
		int position = 0;
		for(Burst b : bursts){
			int[] burstState = b.getBurstState();
			for(int i = 0; i < burstState.length; i++){
				processState[i + position] = burstState[i];
			}
			position += burstState.length;
		}
		
	}
	
	public void step(){
		runtime++;
		//if(processState[runtime] == CS){
			
		//}
	}
	
	public int getRuntime(){
		return runtime;
	}
	
	public boolean isDone(){
		return runtime >= estimatedTotalRuntime; 
	}
	
	public int getEstimatedTotalRuntime(){
		return estimatedTotalRuntime;
	}
}
