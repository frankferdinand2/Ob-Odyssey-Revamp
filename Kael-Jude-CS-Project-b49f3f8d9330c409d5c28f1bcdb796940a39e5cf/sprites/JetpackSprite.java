import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class JetpackSprite implements DisplayableSprite {

    private static final String IMAGE_PATH = "res/SpriteImages/Jetpack/JetpackSprite1.png";
    private static final String FRAME1_PATH = "res/SpriteImages/Jetpack/JetpackSprite2.png";
    private static final String FRAME2_PATH = "res/SpriteImages/Jetpack/JetpackSprite3.png";
    private static final String FRAME3_PATH = "res/SpriteImages/Jetpack/JetpackSprite4.png";

    private static final double DEFAULT_WIDTH = 119.0;
    private static final double DEFAULT_HEIGHT = 53.0;
    
    private static Image normalImage;
    private static Image frame1Image;
    private static Image frame2Image;
    private static Image frame3Image;

    private Image currentImage;
    private double centerX;
    private double centerY;
    private double width;
    private double height;
    private boolean dispose;
    private boolean reversed;
    private int timeKeyUp = 0;
    public JetpackSprite(double centerX, double centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        // Load images once
        try {
            if (normalImage == null) {
            	normalImage = ImageIO.read(new File(IMAGE_PATH));
            }
            if (frame1Image == null) {
            	frame1Image = ImageIO.read(new File(FRAME1_PATH));
            }
            if (frame2Image == null) {
            	frame2Image = ImageIO.read(new File(FRAME2_PATH));
            }
            if (frame3Image == null) {
            	frame3Image = ImageIO.read(new File(FRAME3_PATH));
            }

        } catch (IOException e) {
            System.err.println("Error loading image: " + e);
        }

        currentImage = normalImage; // start with normal image
    }

    public Image getImage() {
        return currentImage;
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
    public void setDispose(boolean dispose) { this.dispose = dispose; }

    public void update(Universe universe, long actualDeltaTime) {
        double deltaTime = actualDeltaTime * 0.001;
        Image imageOn;
        boolean flappyMode = false;
        

        for (DisplayableSprite sprite : universe.getSprites()) {
        	if (sprite instanceof ObSprite) {
        		centerX = sprite.getCenterX();
        		centerY = sprite.getCenterY();
        		dispose = sprite.getDispose();
        		reversed = ((ObSprite) sprite).getReversed();
        		flappyMode = ((ObSprite) sprite).getFlappyMode();
        	}
        }
        
        KeyboardInput keyboard = KeyboardInput.getKeyboard();
        
		boolean animation = false;
		
        for (DisplayableSprite sprite : universe.getSprites()) {
        	if (sprite instanceof ObSprite) {
        		if (((ObSprite) sprite).getJetBattery() > 0) {
        			animation = true;
        		}
        	}
        }
        
        if (animation) {
        	 if (keyboard.keyDown(38) && !flappyMode) {
             	timeKeyUp++;
             }
             else {
             	timeKeyUp = 0;
             }
             
             
             if (timeKeyUp == 0) {
             	imageOn = normalImage;
             }
             else if (timeKeyUp > 3) {
             	int check = timeKeyUp % 10;
             	if (check > 5) {
             		imageOn = frame3Image;
             	}
             	else {
             		imageOn = frame2Image;
             	}
             }
             else {
             	imageOn = frame1Image;
             }
             
             
             if (keyboard.keyDownOnce(38) && flappyMode) {
             	imageOn = frame3Image;
             }
        }
        else {
        	imageOn = normalImage;
        }
       
        
        if (reversed) {
        	currentImage = ImageRotator.rotate(imageOn, 270);
        }
        else {
        	currentImage = imageOn;
        }
    }


    
}
