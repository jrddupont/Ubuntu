package utils;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;


public class Scheduler2 {
	
	Process[] processes;
	
	//static TreeMap<Long, ArrayList<Process>> schedulingQueue = new TreeMap<Long, ArrayList<Process>>();
	static TreeSet<TimePair> schedulingQueue = new TreeSet<TimePair>();
	
	//Smallest time slice possible
	static int minGranularity = 1;
	
	//"Targeted preemption latency for CPU-bound tasks"
	static int schedLatency = 1;

	Scheduler2( Process[] processes ){
		this.processes = processes;
		
		//Store all processes by their "virtual runtime" aka total runtime they have had, which is probably 0 if they're new
		for( int i = 0; i<this.processes.length; i++ ){
			Process proc = this.processes[i];
			TimePair tp = new TimePair(proc);
			tp.virtualRuntime = getVirtualRuntime(proc); // Don't all processes start at 0?
			schedulingQueue.add(new TimePair(proc));
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
	
	
	//Period of time in which all processes run once (hopefully but maybe not always)
	private int getPeriod(){
		return minGranularity * schedulingQueue.size();
	}
	
	//Returns normalized runtime used in sorting schedulingQueue red/black tree
	private double getNormalizedVirtualRunTime( Process process ){
		
		//Largest value in r/b is item with longest runtime
		
		Long largestRuntime = schedulingQueue.last().virtualRuntime;
		
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
		for(TimePair tp : schedulingQueue){
			totalNice += getNiceValue(tp.process);
		}
		return totalNice;
	}
	
	//Get the next process to be run aka the one with lowest execution time
	private Process getNextProcess(){
		try{
			return schedulingQueue.first().process;
		} catch( NoSuchElementException e ){
			return null;
		}
	}
	
	
	private class TimePair{
		public long virtualRuntime = 0;
		public Process process;
		public TimePair(Process p){
			process = p;
		}
		
		@Override
		public boolean equals(Object o){
			if(o instanceof Process){
				return o == process;
			}
			return o == this;
		}
	}
}