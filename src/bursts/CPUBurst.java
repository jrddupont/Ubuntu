package bursts;

public class CPUBurst extends Burst{
	public boolean hasCS;
	public int startBurst;
	public int csBurst;
	public int endBurst;
	public CPUBurst(int start, int cs, int end){
		startBurst = start;
		csBurst = cs;
		endBurst = end;
		hasCS = true;
	}
	public CPUBurst(int burst){
		startBurst = burst;
		hasCS = false;
	}
}
