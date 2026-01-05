
public class ShellAnimation implements Animation {

	private Universe current = new ShellUniverse();
	private boolean universeSwitched = false;
	private boolean animationComplete = false;
	
	public Universe getCurrentUniverse() {
		return current;
	}
	
	@Override
	public boolean getUniverseSwitched() {
		return universeSwitched;
	}

	@Override
	public void acknowledgeUniverseSwitched() {
		this.universeSwitched = false;		
	}

	@Override
	public boolean isComplete() {
		return animationComplete;
	}

	@Override
	public void setComplete(boolean complete) {
		this.animationComplete = true;		
	}

	@Override
	public void update(AnimationFrame frame, long actual_delta_time) {		
		if ( KeyboardInput.getKeyboard().keyDownOnce(27)) {
			animationComplete = true;
		}				
	}

	public Universe switchUniverse(Object event) {
		animationComplete = true;
		return current;
	}
	
}
