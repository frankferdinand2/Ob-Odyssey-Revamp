
public interface Animation {
	
	public Universe getCurrentUniverse();
	
	public Universe switchUniverse(Object event);
		
	public boolean getUniverseSwitched();

	public void acknowledgeUniverseSwitched();

	public boolean isComplete();

	public void setComplete(boolean complete);
	
	public void update(AnimationFrame frame, long actual_delta_time);	
	
}
