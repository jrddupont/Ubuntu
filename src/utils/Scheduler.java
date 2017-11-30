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

	Scheduler( Process[] processes ){
		Scheduler.processes = processes;
		
		schedulingQueue = new TreeSet<TimePair>();
		
		//Store all processes by their "virtual runtime" aka total runtime they have had, which is probably 0 if they're new
		for( int i = 0; i<Scheduler.processes.length; i++ ){
			Process proc = Scheduler.processes[i];
			TimePair tp = new TimePair(proc);
			//tp.virtualRuntime = i; // Don't all processes start at 0?
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
			
			//AAAAAAAAAAAAAAAAHHHHHHHHHHH
			if( currentProcess.isDone() ){
				continue;
			}
			
			
			int timeSlice = getTimeSlice( currentProcess );
			
			System.out.println( "Process #" + currentProcess.id + " has started running with runtime of " + currentProcess.getRuntime() + " out of a total " + currentProcess.getEstimatedTotalRuntime() + " runtime" );
			System.out.println( "Process #" + currentProcess.id + " has timeslice " + timeSlice );
			System.out.println( "There are currently " + schedulingQueue.size() + " items in the scheduling queue" );
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
				
				//After we've finished stepping, check if we just finished so we can stop trying to step.
				if( currentProcess.isDone() ){
					System.out.println( "Process #" + currentProcess.id + " is finished" );
					
					//Break out of the timeslice for loop
					break;
				}
			}
			
			//Remove the process from the scheduling queue
			//We're doing it here so that nice values are handled properly.
			schedulingQueue.pollFirst();
			
			if ( currentProcess.isDone() ){
				
				//If we've finished, we need to deallocate this 
				MemoryManager.deallocate( currentProcess );
				
			}else{
				
				//If the process is still "good" we should update the virtual runtime and add it back into the tree.
				
				//Update process' TimePair's Virtual Runtime
				currentTimePair.virtualRuntime = getVirtualRuntime( currentProcess );
				
				//Insert TimePair into the scheduling queue
				schedulingQueue.add( currentTimePair );
				
			}
			
		}
	}
	
	private int getTimeSlice( Process process ){
		
		
		
		double nice = getNiceValue( process );
		double totalNice = getTotalNice();
		
		System.out.println( "Process #" + process.id + " has a nice value of " + nice + " and a totalNice of " + totalNice + " with a period of " + getPeriod() );
		
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
		
//		double largestRuntime = schedulingQueue.last().virtualRuntime;
//		
//		//Avoid infinity
//		if( largestRuntime == 0 ){
//			return 0;
//		}
//		
//		double virtualRuntime = ( (double) process.getRuntime() / (double) largestRuntime  );
		
		return process.getRuntime();
		
		//return virtualRuntime;
	}
	
	
	//"Nice values" indicate the priority of a process from -20 to 19 where lower is higher priority
	//For our purposes here, I'll be converting IDs to a value that can be easily made into a fraction later for time slice calculations
	//Also of note is that process priority = process ID
	private int getNiceValue( Process process ){
		int maxID = 0;
		for( TimePair tp : schedulingQueue ){
			if( tp.process.id > maxID ){
				maxID = tp.process.id;
			}
		}
		
		double nice = process.id - ( maxID + 1 );
		
		return (int) Math.abs( nice );
	}
	
	//Total of all nice values in the run queue
	private double getTotalNice(){
		double totalNice = 0;
		for(TimePair tp : schedulingQueue){
			
			double nice = getNiceValue( tp.process );
			
			totalNice += nice;
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
