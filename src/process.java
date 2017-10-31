
public class Process {
	int id;
	int ioTime;
	int cpuTime;
	int memory;
	
	Process( int id, int ioTime, int cpuTime, int memory ){
		this.id = id;
		this.ioTime = ioTime;
		this.cpuTime = cpuTime;
		this.memory = memory;
	}
}
