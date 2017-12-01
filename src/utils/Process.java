package utils;
import java.util.ArrayList;

import bursts.Burst;
import bursts.CPUBurst;
import bursts.IOBurst;


public class Process {
	public static final int IO = 0;
	public static final int CPU = 1;
	public static final int CS = 2;
	public static final int DONE = 3;
	public int id;
	public int memory;
	private int runtime = 0;
	public int age = 0;
	private int estimatedTotalRuntime = 0;
	ArrayList<Burst> bursts = new ArrayList<Burst>();
	
	public Process( int id, int memory, ArrayList<Burst> newBursts ){
		this.id = id;
		this.memory = memory;
		bursts.addAll(newBursts);
		
		for(Burst b : bursts){
			estimatedTotalRuntime += b.getEstimatedTime();
		}
	}
	
	public int getCurrentState(){
		if(isDone()){
			return DONE;
		}
		Burst b = getCurrentBurst();
		if(b instanceof IOBurst){
			return IO;
		}
		if(b instanceof CPUBurst && ((CPUBurst) b).hasCS()){
			return CPU;
		}
		CPUBurst cpub = (CPUBurst) b;
		int currentTotal = 0;
		for(Burst curBurst : bursts){
			if(curBurst == b){
				break;
			}
			currentTotal += curBurst.getEstimatedTime();
			
		}
		currentTotal = runtime - (currentTotal - cpub.getEstimatedTime());
		return cpub.getCurrentState(currentTotal);
	}
	
	// Gets the burst that the process is currently in
	private Burst getCurrentBurst(){
		if(bursts.isEmpty()){	
			return null;
		}
		int currentTotal = 0;
		for(Burst b : bursts){
			currentTotal += b.getEstimatedTime();
			if(currentTotal > runtime){
				return b;
			}
		}
		return null;
	}
	
	// increments the runtime and if it steps into a CS that is locked, it will throw an error
	private int lastState = CPU;
	public void step() throws SharedResourceException{
		runtime++;
		age++;
		Driver.globalTime++;
		
		int newState = getCurrentState();
		if(newState == CS && newState != lastState){
			boolean response = ProcessSynchronizer.lock(((CPUBurst)getCurrentBurst()).getResource());
			if(!response){
				runtime--;
				throw new SharedResourceException("Can't step into CS, resource is locked");
			}
		}else if(lastState == CS && newState != CS){
			ProcessSynchronizer.signal(((CPUBurst)getCurrentBurst()).getResource());
		}

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
	
	// Custom exception for attempted CS access 
	@SuppressWarnings("serial")
	public class SharedResourceException extends Exception{
	      public SharedResourceException(String message){
	         super(message);
	      }
	 } 
}
