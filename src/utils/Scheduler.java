package utils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;

import utils.Process.SharedResourceException;


public class Scheduler {
	
	public static Process[] processes;
	
	//static TreeMap<Long, ArrayList<Process>> schedulingQueue = new TreeMap<Long, ArrayList<Process>>();
	static TreeSet<TimePair> schedulingQueue;
	
	//Smallest time slice possible
	static int minGranularity = 3;

	Scheduler( Process[] processes ){
		this.processes = processes;
		
		schedulingQueue = new TreeSet<TimePair>(new Comparator<TimePair>() {
			@Override
			public int compare(TimePair o1, TimePair o2) {
				return (int) (o2.virtualRuntime - o1.virtualRuntime) + 1;
			}
		});
		
		//Store all processes by their "virtual runtime" aka total runtime they have had, which is probably 0 if they're new
		for( int i = 0; i<this.processes.length; i++ ){
			Process proc = this.processes[i];
			TimePair tp = new TimePair(proc);
			//tp.virtualRuntime = i; // Don't all processes start at 0?
			System.out.println( "ADDDDDING" );
			schedulingQueue.add(tp);
			System.out.println( schedulingQueue.size() );
			for(TimePair tp2 : schedulingQueue){
				System.out.println(tp2.virtualRuntime);
			}
		}
		
	}
	
	//Actually start
	public void start(){
		//Primary execution loop
		outerExecution: 
		while( !schedulingQueue.isEmpty() ){
			
			//Get the current process
			Process currentProcess = getNextProcess();
			
			//Also get the current process' TimePair
			TimePair currentTimePair = schedulingQueue.first();
			
			//Remove the process from the scheduling queue
			schedulingQueue.remove( currentTimePair );
			
			int timeSlice = getTimeSlice( currentProcess );
			
			System.out.println( timeSlice );
			
			//Execute this process' time slice
			for( int i = 0; i < timeSlice; i++ ){
				System.out.println( "Process #" + currentProcess.id + " has started running" );
				
				//Advance time
				try {
					MemoryManager.run( currentProcess );
				} catch (SharedResourceException e) {
					
					//Can't advance time
					System.out.println( "Process ID " + currentProcess.id + " could not run because access to critical section was locked" );
					
					//Update process' TimePair's Virtual Runtime
					currentTimePair.virtualRuntime = getVirtualRuntime( currentProcess );
					
					
					//Insert TimePair into the scheduling queue
					schedulingQueue.add( currentTimePair );
					
					
					//Continue onto the next process in the queue
					continue outerExecution;
				}
				
				//After we've finished stepping, check if we just finished
				if( currentProcess.isDone() ){
					//If we've finished, we need to deallocate this 
					
					MemoryManager.deallocate( currentProcess );
					
					//Continue onto the next process in the queue
					continue outerExecution;
					
				}
				
			}
			
		}
	}
	
	private int getTimeSlice( Process process ){
		
		double nice = getNiceValue( process );
		double totalNice = getTotalNice();
		
		System.out.println( "nice " + nice );
		System.out.println( "totalNice " + totalNice );
		
		double slice = getPeriod() * ( nice / totalNice );
		
		return (int) Math.ceil( slice );
	}
	
	
	//Period of time in which all processes run once (hopefully but maybe not always)
	private int getPeriod(){
		return minGranularity * schedulingQueue.size();
	}
	
	//Returns normalized runtime used in sorting schedulingQueue red/black tree
	private double getVirtualRuntime( Process process ){
		
		//Largest value in r/b is item with longest runtime
		
		double largestRuntime = schedulingQueue.last().virtualRuntime;
		
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
	private int getNiceValue( Process process ){
		int maxID = 0;
		System.out.println( schedulingQueue.size() );
		for( TimePair tp : schedulingQueue ){
			System.out.println( "loooooop" );
			if( tp.process.id > maxID ){
				maxID = tp.process.id;
			}
		}
		
		System.out.println( "max" + maxID );
		
		return process.id - ( maxID + 1 );
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
		public double virtualRuntime = 0;
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
