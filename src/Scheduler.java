import java.util.ArrayList;
import java.util.TreeMap;


public class Scheduler {
	
	Process[] processes;
	
	MemoryManager memory = new MemoryManager();
	
	static TreeMap<Long, ArrayList<Process>> schedulingQueue = new TreeMap<Long, ArrayList<Process>>();

	Scheduler( Process[] processes ){
		this.processes = processes;
		
		Process a = new Process(1, 0, 0, 0  );
		Process b = new Process(2, 0, 0, 0  );
		
		
		
		StoreProcess( a, 0 );
		StoreProcess( b, 100 );
		
		System.out.println( schedulingQueue.get( schedulingQueue.firstKey() ).get(1).id );
	}
	
	private void StoreProcess( Process process, long executionTime ){
		
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
