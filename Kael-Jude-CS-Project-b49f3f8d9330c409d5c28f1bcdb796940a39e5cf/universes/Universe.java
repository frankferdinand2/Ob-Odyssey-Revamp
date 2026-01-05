import java.util.ArrayList;

public interface Universe {

	//STATIC VARIABLES
	
	//INSTANCE VARIABLES

	public double getScale();

	public double getXCenter();
	public double getYCenter();
	
	public void setXCenter(double xCenter);
	public void setYCenter(double yCenter);
	
	public boolean isComplete();
	public void setComplete(boolean complete);
	
	public ArrayList<DisplayableSprite> getSprites();	
	public ArrayList<Background> getBackgrounds();		

	public void update(Animation animation, long actual_delta_time);
    	
}
