package utils;
import java.util.Comparator;
import java.util.TreeSet;

import utils.Process.SharedResourceException;


public class Scheduler {
	
	public static Process[] processes;
	
	//static TreeMap<Long, ArrayList<Process>> schedulingQueue = new TreeMap<Long, ArrayList<Process>>();
	static TreeSet<TimePair> schedulingQueue;
	
	//Smallest time slice possible
	static int minGranularity = 3;
	
	//"Targeted preemption latency for CPU-bound tasks"
	static int schedLatency = 1;

	Scheduler( Process[] processes ){
		Scheduler.processes = processes;
		
		schedulingQueue = new TreeSet<TimePair>(new Comparator<TimePair>() {
			@Override
			public int compare(TimePair o1, TimePair o2) {
				return (int) (o2.virtualRuntime - o1.virtualRuntime);
			}
		});
		
		//Store all processes by their "virtual runtime" aka total runtime they have had, which is probably 0 if they're new
		for( int i = 0; i<Scheduler.processes.length; i++ ){
			Process proc = Scheduler.processes[i];
			TimePair tp = new TimePair(proc);
			//tp.virtualRuntime = getVirtualRuntime(proc); // Don't all processes start at 0?
			schedulingQueue.add(tp);
		}
		
		//TESTING RANDOM PROCESS STEPS
		for (int i = 0; i < Scheduler.processes.length; i++) {
			int num = (int) ( 3 + Math.random() * 10 );
			for (int j = 0; j < num; j++) {
				try {
					Scheduler.processes[i].step();
				} catch (Exception e) {
					// TODO Prempt the process 
				}
			}
		}
		
		System.out.println( getTimeSlice( getNextProcess() ) );
		
		
		//Primary execution loop
		while( !schedulingQueue.isEmpty() ){
			Process currentProcess = getNextProcess();
			
			int timeSlice = getTimeSlice( currentProcess );
			
			//Advance time
			try {
				MemoryManager.run( currentProcess );
			} catch (SharedResourceException e) {
				System.out.println( "Process ID " + currentProcess.id + " could not run because access to critical section was locked" );
			}
			
			
			
			
		}
		
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
		return schedulingQueue.first().process;
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
