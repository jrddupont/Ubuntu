import java.util.ArrayList;

import bursts.Burst;


public class Process {
	int id;
	int memory;
	ArrayList<Burst> bursts = new ArrayList<Burst>();
	Process( int id, int memory, ArrayList<Burst> newBursts ){
		this.id = id;
		this.memory = memory;
		bursts.addAll(newBursts);
	}
}
