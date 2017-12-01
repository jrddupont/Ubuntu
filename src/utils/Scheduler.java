package utils;
import java.util.ArrayList;
import java.util.TreeSet;

import utils.Process.SharedResourceException;


public class Scheduler {

	public static Process[] processes;

	//static TreeMap<Long, ArrayList<Process>> schedulingQueue = new TreeMap<Long, ArrayList<Process>>();
	static TreeSet<TimePair> schedulingQueue;
	static ArrayList<TimePair> doneProcesses;

	//Smallest time slice possible
	static int minGranularity = 3;

	Scheduler( Process[] processes ){
		Scheduler.processes = processes;

		schedulingQueue = new TreeSet<TimePair>();
		doneProcesses = new ArrayList<Scheduler.TimePair>();

		//Store all processes by their "virtual runtime" aka total runtime they have had, which is probably 0 if they're new
		for( int i = 0; i<Scheduler.processes.length; i++ ){
			Process proc = Scheduler.processes[i];
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
				//Also get the current process' TimePair
				TimePair currentTimePair = schedulingQueue.first();

				int timeSlice = getTimeSlice( currentTimePair.process );

				//Execute this process' time slice
				//Print info to console
				System.out.println( "\n@time: " + Driver.globalTime );
				System.out.println("  CPU: P" + currentTimePair.process.id + " running.");
				String readyQueueString = "";
				for(TimePair tp : schedulingQueue){
					if(tp == schedulingQueue.first()){
						continue;
					}
					readyQueueString += "P" + tp.process.id + " ";
				}
				String doneProcessString = "";
				for(TimePair tp : doneProcesses){
					doneProcessString += "P" + tp.process.id + " ";
				}
				System.out.println( "    Ready queue: " + readyQueueString );
				if(doneProcesses.size() > 0){
					System.out.println( "    Done processes: " + doneProcessString );
				}

				System.out.print("    CPU events: ");
				for( int i = 0; i < timeSlice; i++ ){

					//Advance time and execute
					try {
						MemoryManager.run( currentTimePair.process );
					} catch (SharedResourceException e) {

						//Can't advance time
						System.out.print( "P" + currentTimePair.process.id + " prempted due to CS lock, " );

						//Continue onto the next process in the queue
						continue outerExecution;
					}

					//After we've finished stepping, check if we just finished so we can stop trying to step.
					if( currentTimePair.process.isDone() ){
						doneProcesses.add(currentTimePair);
						System.out.print( "P" + currentTimePair.process.id + " finished, " );

						//Break out of the timeslice for loop
						break;
					}
				}
				System.out.println();
				
				MemoryManager.printDebug();
				
				System.out.print("    Memory events:");
				//Remove the process from the scheduling queue
				//We're doing it here so that nice values are handled properly.
				schedulingQueue.pollFirst();

				if ( currentTimePair.process.isDone() ){

					//If we've finished, we need to deallocate this 
					MemoryManager.deallocate( currentTimePair.process );

				}else{

					//If the process is still "good" we should update the virtual runtime and add it back into the tree.

					//Update process' TimePair's Virtual Runtime
					currentTimePair.virtualRuntime = getVirtualRuntime( currentTimePair.process );

					//Insert TimePair into the scheduling queue
					schedulingQueue.add( currentTimePair );

				}
				System.out.println();
				

			}
	}

	//How long a process runs for
	private int getTimeSlice( Process process ){

		double nice = getNiceValue( process );
		double totalNice = getTotalNice();

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
