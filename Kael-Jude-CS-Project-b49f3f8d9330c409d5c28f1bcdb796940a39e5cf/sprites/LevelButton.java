import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LevelButton implements DisplayableSprite {
  
	private static final String IMAGE_PATH = "res/SpriteImages/PlayButton.png";
	private static final double WIDTH = 100;
	private static final double HEIGHT = 100;

	private static Image image;
	
	private double centerX;
	private double centerY;
	private double width;
	private double height;
	private boolean dispose;
	private String levelPath = "";
	
	public LevelButton(double centerX, double centerY, String levelPath) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = WIDTH;
		this.height = HEIGHT;
		loadImage();
		this.levelPath = levelPath;
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
	
	public String getLevelPath() {
		return levelPath;
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

	}
	
	public boolean isClicked() {
	    if (!MouseInput.leftButtonDown) {
	        return false;
	    }

	    double x = MouseInput.logicalX;
	    double y = MouseInput.logicalY;

	    return x >= getMinX() && x <= getMaxX() && y >= getMinY() && y <= getMaxY();
	}
			
}
