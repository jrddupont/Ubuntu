package bursts;

public class IOBurst extends Burst{
	private int burst;
	public IOBurst(int burst){
		this.burst = burst;
	}
	@Override
	public int getEstimatedTime(){
		return burst;
	}
}
