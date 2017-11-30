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
		
		schedulingQueue = new TreeSet<TimePair>();
		
		//Store all processes by their "virtual runtime" aka total runtime they have had, which is probably 0 if they're new
		for( int i = 0; i<this.processes.length; i++ ){
			Process proc = this.processes[i];
			TimePair tp = new TimePair(proc);
			//tp.virtualRuntime = i; // Don't all processes start at 0?
			schedulingQueue.add(tp);
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
			schedulingQueue.pollFirst();
			
			//AAAAAAAAAAAAAAAAHHHHHHHHHHH
			if( currentProcess.isDone() ){
				continue;
			}
			
			
			int timeSlice = getTimeSlice( currentProcess );
			
			System.out.println( "Process #" + currentProcess.id + " has started running with runtime of " + currentProcess.getRuntime() + " out of a total " + currentProcess.getEstimatedTotalRuntime() + " runtime" );
			System.out.println( "Process #" + currentProcess.id + " has timeslice " + timeSlice );
			//Execute this process' time slice
			for( int i = 0; i < timeSlice; i++ ){
				
				//Advance time and execute
				try {
					System.out.println( "Process #" + currentProcess.id + " has stepped step " + i );
					MemoryManager.run( currentProcess );
				} catch (SharedResourceException e) {
					
					//Can't advance time
					System.out.println( "Process ID " + currentProcess.id + " could not run because access to critical section was locked" );
					
					//Continue onto the next process in the queue
					continue outerExecution;
				}
				
				//After we've finished stepping, check if we just finished
				if( currentProcess.isDone() ){
					System.out.println( "Process #" + currentProcess.id + " is finished" );
					//If we've finished, we need to deallocate this 
					
					MemoryManager.deallocate( currentProcess );
					
					//Continue onto the next process in the queue
					continue outerExecution;
					
				}else{
					
					//Update process' TimePair's Virtual Runtime
					currentTimePair.virtualRuntime = getVirtualRuntime( currentProcess );
					
					System.out.println( "Process #" + currentProcess.id + " has loaded into the scheduling queue" );
					
					//Insert TimePair into the scheduling queue
					schedulingQueue.add( currentTimePair );
				}
				
			}
		}
	}
	
	private int getTimeSlice( Process process ){
		
		double nice = getNiceValue( process );
		double totalNice = getTotalNice();
		
		System.out.println( "AAAAAAAAAAAAH " + (double) getPeriod() );
		
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
		
		return process.getRuntime();
		
		//return virtualRuntime;
	}
	
	
	//"Nice values" indicate the priority of a process from -20 to 19 where lower is higher priority
	//For our purposes here, I'll be doing a simple slide conversion from priority 1->-20, 2->-19, etc.
	//Also of note is that process priority = process ID
	private int getNiceValue( Process process ){
		int maxID = 0;
		for( TimePair tp : schedulingQueue ){
			if( tp.process.id > maxID ){
				maxID = tp.process.id;
			}
		}
		
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
	
	
	private class TimePair implements Comparable<TimePair>{
		public double virtualRuntime = 0;
		public Process process;
		public TimePair(Process p){
			process = p;
		}
		
		@Override
		public int compareTo( TimePair se ){
			if( this.virtualRuntime == se.virtualRuntime ){
				return 1;
			}
			
			return (int) (this.virtualRuntime - se.virtualRuntime);
		}
	}
}
