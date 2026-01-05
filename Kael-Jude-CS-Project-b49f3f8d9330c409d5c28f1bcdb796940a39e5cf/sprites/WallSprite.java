import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class WallSprite implements DisplayableSprite {
  
	private static final String IMAGE_PATH = "res/67.jpeg";
	private static final double DEFAULT_WIDTH = 720.0;
	private static final double DEFAULT_HEIGHT = 1280.0;
	
	private static Image image;
	
	private double originalWallSpeed;
	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private boolean dispose;
	private boolean wallStart = false;
	private double wallSpeed;
	private double obSpeed;

	public WallSprite(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = DEFAULT_WIDTH;
		this.height = DEFAULT_HEIGHT;
		loadImage();
	}


	private void loadImage() {
		if (image == null) {
			try {
				image = ImageIO.read(new File(IMAGE_PATH));
			} catch (IOException e) {
				System.err.println("Error loading image: " + e);
			}
		}
	}

	public Image getImage() {
		return image;
	}

	public boolean getVisible() {
		return true;
	}

	public double getMinX() {
		return centerX - (width / 2);
	}

	public double getMaxX() {
		return centerX + (width / 2);
	}

	public double getMinY() {
		return centerY - (height / 2);
	}

	public double getMaxY() {
		return centerY + (height / 2);
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public boolean getDispose() {
		return dispose;
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}


	@Override
	public void setDispose(boolean dispose) {
		this.dispose = dispose;
	}
	
	public void update(Universe universe, long actualDeltaTime) {
		double deltaTime = actualDeltaTime * 0.001;
		
		ShellUniverse u = (ShellUniverse) universe;
	    obSpeed = u.getObSpeed();
	    originalWallSpeed = u.getWallSpeed();
	    
		if (wallStart) {
			centerX += wallSpeed * deltaTime;
		}
		
	    boolean flappyModeActive = false;
	    
	    for (DisplayableSprite sprite : universe.getSprites()) {
	        if (sprite instanceof ObSprite) {
	            flappyModeActive = ((ObSprite) sprite).getFlappyMode();
	            break;
	        }
	    }
		
	    KeyboardInput keyboard = KeyboardInput.getKeyboard();
	    
	    if (flappyModeActive) {
	    	wallSpeed = obSpeed;
	    }
	    else {
	    	wallSpeed = originalWallSpeed;
	    }

	    if (keyboard.keyDown(39) || flappyModeActive) { // ob moves right objects move left
    	   centerX -= obSpeed * deltaTime;
    	   wallStart = true;
        }
	    
	    if (keyboard.keyDown(37) && !flappyModeActive) { // and vice versa
    	   centerX += obSpeed * deltaTime;
    	   wallStart = true;
	    }
	}		
}
