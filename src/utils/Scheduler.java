package utils;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.TreeMap;


public class Scheduler {
	
	Process[] processes;
	
	static TreeMap<Long, ArrayList<Process>> schedulingQueue = new TreeMap<Long, ArrayList<Process>>();

	Scheduler( Process[] processes ){
		this.processes = processes;
		
		Process a = new Process(1, 0, new ArrayList() );
		Process b = new Process(2, 0, new ArrayList() );
		
		getNextProcess();
		
		storeProcess( a, 0 );
		storeProcess( b, 0 );
		
		System.out.println( schedulingQueue.get( schedulingQueue.firstKey() ).get(0).id );
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
	private void storeProcess( Process process, long executionTime ){
		
		ArrayList<Process> list;
		
		//If this node doesn't exist, make an arraylist and store that
		if( !schedulingQueue.containsKey( executionTime ) ){
			list = new ArrayList<Process>();
		}else{
			list = schedulingQueue.get( executionTime );
		}
		
		list.add( process );
		
		schedulingQueue.put( executionTime ,  list );
	}
}
