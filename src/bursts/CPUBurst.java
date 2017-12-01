package bursts;

import utils.Process;

public class CPUBurst extends Burst{
	private boolean hasCS = false;
	private int startBurst = 0;
	private int csBurst = 0;
	private int endBurst = 0;
	private int resource;
	public CPUBurst(int start, int cs, int end, int csID){
		startBurst = start;
		csBurst = cs;
		endBurst = end;
		hasCS = true;
		this.resource = csID;
	}
	public CPUBurst(int burst){
		startBurst = burst;
		hasCS = false;
	}
	@Override
	public int getEstimatedTime(){
		return startBurst + csBurst + endBurst;
	}
	public boolean hasCS(){
		return hasCS;
	}
	public int getResource(){
		return resource;
	}
	public int getCurrentState(int currentTotal) {
		if(currentTotal >= startBurst && currentTotal < startBurst + csBurst){
			return Process.CS;
		}
		return Process.CPU;
	}
}
