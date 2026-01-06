import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SpikeSprite implements DisplayableSprite {

    private static final String DEFAULT_IMAGE_PATH = "res/red.jpg";
    private Image image;

    private double centerX;
    private double centerY;
    private double width;
    private double height;
    private boolean dispose;
    private double obSpeed;
    private String imagePath;
    private double velocityX;

    public SpikeSprite(double centerX, double centerY, double width, double height) {
        this(centerX, centerY, width, height, DEFAULT_IMAGE_PATH);
    }

    public SpikeSprite(double centerX, double centerY, double width, double height, String imagePath) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
        this.imagePath = imagePath;
        loadImage(imagePath);
    }
    
    
    public String getImagePath() {
    	return imagePath;
    }
    
    private void loadImage(String imagePath) {
        try {
            image = ImageIO.read(new File(imagePath));
            if (image == null) {
                System.err.println("Image not found: " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + e);
            image = null;
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
	    
		
	    KeyboardInput keyboard = KeyboardInput.getKeyboard();



        if (keyboard.keyDown(39)) { // ob moves right objects move left
        	velocityX -= obSpeed * deltaTime;
            centerX += velocityX * deltaTime;
        }
        
        else if (keyboard.keyDown(37)) {
        	velocityX += obSpeed * deltaTime;
            centerX += velocityX * deltaTime;
        }
        
        else  if (velocityX > 0) { // and vice versa 
        	velocityX -= obSpeed * deltaTime;
            centerX += velocityX * deltaTime;
        }
        else if (velocityX < 0) {
        	velocityX += obSpeed * deltaTime;
        	centerX += velocityX * deltaTime;
        }
	}
}
