package bursts;

import utils.Process;

public class CPUBurst extends Burst{
	private boolean hasCS;
	private int startBurst = 0;
	private int csBurst = 0;
	private int endBurst = 0;
	private int csID;
	public CPUBurst(int start, int cs, int end, int csID){
		startBurst = start;
		csBurst = cs;
		endBurst = end;
		hasCS = true;
		this.csID = csID;
	}
	public CPUBurst(int burst){
		startBurst = burst;
		hasCS = false;
	}
	@Override
	public int getEstimatedTime(){
		return startBurst + csBurst + endBurst;
	}
	@Override
	public int[] getBurstState(){
		int[] output = new int[getEstimatedTime()];
		for(int i = 0; i < output.length; i++){
			output[i] = Process.CPU;
		}
		if(hasCS){
			for(int i = startBurst; i < startBurst + csBurst; i++){
				output[i] = Process.CS;
			}
		}
		return output;
	}
}
