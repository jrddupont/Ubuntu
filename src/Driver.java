import java.util.ArrayList;

import bursts.Burst;
import bursts.CPUBurst;
import bursts.IOBurst;


public class Driver {
	public static void main(String[] args){
		ArrayList<Burst> p1Bursts = new ArrayList<Burst>();
		p1Bursts.add(new CPUBurst(3));
		p1Bursts.add(new IOBurst(14));
		p1Bursts.add(new CPUBurst(3, 2, 2));
		
		ArrayList<Burst> p2Bursts = new ArrayList<Burst>();
		p2Bursts.add(new CPUBurst(5));
		p2Bursts.add(new IOBurst(7));
		p2Bursts.add(new CPUBurst(1, 2, 1));
		
		ArrayList<Burst> p3Bursts = new ArrayList<Burst>();
		p3Bursts.add(new CPUBurst(6));
		
		Process[] processes = {
				new Process(1, 64, p1Bursts),
				new Process(2, 32, p2Bursts),
				new Process(3, 32, p3Bursts),
		};
		
		new Scheduler(processes);
	}
}

