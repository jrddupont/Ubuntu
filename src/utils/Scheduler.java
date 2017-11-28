package utils;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;


public class Scheduler {
	
	Process[] processes;
	
	static TreeMap<Long, ArrayList<Process>> schedulingQueue = new TreeMap<Long, ArrayList<Process>>();
	
	//Smallest time slice possible
	static int minGranularity = 1;
	
	//"Targeted preemption latency for CPU-bound tasks"
	static int schedLatency = 1;

	Scheduler( Process[] processes ){
		this.processes = processes;
		
		//Store all processes by their "virtual runtime" aka total runtime they have had, which is probably 0 if they're new
		for( int i = 0; i<this.processes.length; i++ ){
			Process proc = this.processes[i];
			storeProcess( proc, getVirtualRuntime( proc ) );
		}
		
		//TESTING RANDOM PROCESS STEPS
		for (int i = 0; i < processes.length; i++) {
			int num = (int) ( 3 + Math.random() * 10 );
			for (int j = 0; j < num; j++) {
				processes[i].step();
			}
		}
		
		System.out.println( getTimeSlice( getNextProcess() ) );
		
		
//		//Primary execution loop
//		while( !schedulingQueue.isEmpty() ){
//			Process currentProcess = getNextProcess();
//			
//			//Advance time
//			currentProcess.step();
//			
//			
//		}
		
	}
	
	private int getTimeSlice( Process process ){

		double processWeight = getNiceValue( process );
		double totalWeight= getTotalNice();
		
		double slice = getPeriod() * ( processWeight / totalWeight );
		
		return (int) Math.ceil( slice );
	}
	
	private int getProcessCount(){
		int count = 0;
		for(Entry<Long, ArrayList<Process>> entry : schedulingQueue.entrySet()) {
			ArrayList<Process> list = entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				count++;
			}
		}
		return count;
	}
	
	//Period of time in which all processes run once (hopefully but maybe not always)
	private int getPeriod(){
		int period = minGranularity * getProcessCount();
		return period;
	}
	
	//Returns normalized runtime used in sorting schedulingQueue red/black tree
	private double getNormalizedVirtualRunTime( Process process ){
		
		//Largest value in r/b is item with longest runtime
		Long largestRuntime = schedulingQueue.lastKey();
		
		//Avoid infinity
		if( largestRuntime == 0 ){
			return 0;
		}
		
		double virtualRuntime = ( (double) process.getRuntime() / (double) largestRuntime  );
		
		return virtualRuntime;
	}
	
	
	//"Nice values" indicate the priority of a process from -20 to 19 where lower is higher priority
	//For our purposes here, I'll be doing a simple slide conversion from priority 1->-20, 2->-19, etc.
	//Also of note is that process priority = process ID
	private double getNiceValue( Process process ){
		return process.id - 21;
	}
	
	//Total of all nice values in the run queue
	private double getTotalNice(){
		double totalNice = 0;
		for(Entry<Long, ArrayList<Process>> entry : schedulingQueue.entrySet()) {
			ArrayList<Process> list = entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				totalNice += getNiceValue( list.get( i ) );
			}
		}
		return totalNice;
	}
	
	//Get the next process to be run aka the one with lowest execution time
	private Process getNextProcess(){
		try{
			return schedulingQueue.get( schedulingQueue.firstKey() ).get( 0 );
		} catch( NoSuchElementException e ){
			return null;
		}
	}
	
	//Remove process from schedulingQueue
	private void removeProcess( Process process ){
		
		//Tell memory it's gone
		MemoryManager.deallocate( process );
		
	}
	
	//Store a process in the schedulingQueue
	private void storeProcess( Process process, long virtualRunTime ){
		
		ArrayList<Process> list;
		
		//If this node doesn't exist, make an arraylist and store that
		if( !schedulingQueue.containsKey( virtualRunTime ) ){
			list = new ArrayList<Process>();
		}else{
			list = schedulingQueue.get( virtualRunTime );
		}
		
		list.add( process );
		
		schedulingQueue.put( virtualRunTime ,  list );
	}
}
