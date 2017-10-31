
public class Scheduler {
	
	Process[] processes;
	
	MemoryManager memory = new MemoryManager();
	
	Scheduler( Process[] processes ){
		this.processes = processes;
	}
}
