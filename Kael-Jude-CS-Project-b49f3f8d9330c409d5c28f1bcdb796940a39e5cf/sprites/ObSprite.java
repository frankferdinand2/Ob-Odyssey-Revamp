import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObSprite implements DisplayableSprite {

    private static final String IMAGE_PATH = "res/SpriteImages/ObSprite.png";


    private static final double DEFAULT_WIDTH = 85.0;
    private static final double DEFAULT_HEIGHT = 85.0;
    private static final double GROUND_Y = 360.0 - 35.0;
    private static final double BOUNCE_DAMPENING = 0.8;
    private static final double ROOF_Y = -360;
    private static final double MIN_VELOCITY_THRESHOLD = 10.0;
    private static final double DEFAULT_JET_POWER = -1000;
    private static final double DEFAULT_GRAVITY = 450;
    private boolean flappyMode = false;
    private double flapVelocity = -300; 
    
    private double jetBattery = 0;

    private static Image normalImage;

    private Image currentImage;
    private double gravity;
    private double centerX;
    private double centerY;
    private double width;
    private double height;
    private boolean dispose;
    private double velocityY;
    private double jetPower;
    private boolean reversed = false; //switch image based on gravity and portals etc. (reverse gravity flip character and thus flip jetpack too)
    private boolean levelComplete = false;

    public ObSprite(double centerX, double centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.gravity = DEFAULT_GRAVITY;
        this.jetPower = DEFAULT_JET_POWER;

        // Load images once
        try {
            if (normalImage == null) {
            	normalImage = ImageIO.read(new File(IMAGE_PATH));
            }

        } catch (IOException e) {
            System.err.println("Error loading image: " + e);
        }

        currentImage = normalImage; // start with normal image
    }

    public Image getImage() {
        return currentImage;
    }
    
    public boolean getReversed() {
    	return reversed;
    }
    
    public boolean getFlappyMode() {
    	return flappyMode;
    }
    
    public boolean getLevelComplete() {
    	return levelComplete;
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
    
    public void setVelocityY(double velocityY) { 
    	this.velocityY = velocityY; 
	}
    
    public double getJetBattery() {
    	return jetBattery;
    }
    
    @Override
    public void setDispose(boolean dispose) { 
    	this.dispose = dispose; 
	}

    public void update(Universe universe, long actualDeltaTime) {
        double deltaTime = actualDeltaTime * 0.001;
       
		ShellUniverse u = (ShellUniverse) universe;
		if (jetBattery == -1) {
			jetBattery = u.getJetpackBattery();

		}
		
        KeyboardInput keyboard = KeyboardInput.getKeyboard();
        boolean jetActive = keyboard.keyDown(38) && jetBattery > 0;

        if (jetActive && !flappyMode) { 
            velocityY += jetPower * deltaTime;
            jetBattery -= actualDeltaTime;
        }
        
        if (flappyMode) {
            if (keyboard.keyDownOnce(38)) { 
                velocityY = flapVelocity;
            }
        }
             
        velocityY += gravity * deltaTime; // apply gravity
        centerY += velocityY * deltaTime;
        
        // Ground bouncing
        if (centerY + (height/2) >= GROUND_Y) {
            centerY = GROUND_Y - (height/2);
            velocityY = -velocityY * BOUNCE_DAMPENING;
            if (Math.abs(velocityY) < MIN_VELOCITY_THRESHOLD && !keyboard.keyDown(38)) velocityY = 0;
        }

        // Roof bouncing
        if (centerY - (height/2) <= ROOF_Y) {
            centerY = ROOF_Y + (height/2);
            velocityY = -velocityY * BOUNCE_DAMPENING;
            if (Math.abs(velocityY) < MIN_VELOCITY_THRESHOLD && !keyboard.keyDown(38)) velocityY = 0;
        }

        for (DisplayableSprite sprite : universe.getSprites()) {

            if (sprite instanceof ReverseGravityPortalSprite && checkCollision(sprite) && !reversed) {
                reversed = true;        
                flapVelocity = 300;
                gravity = -DEFAULT_GRAVITY;
                jetPower = -DEFAULT_JET_POWER;
                

                currentImage = ImageRotator.rotate(normalImage, 270);
                
            }

            if (sprite instanceof FloorSprite && checkCollision(sprite)) {
                if (velocityY > 0) { // floor possibility
                    centerY = sprite.getMinY() - height / 2;
                    velocityY = -velocityY * BOUNCE_DAMPENING;
                }
                
            	else if (velocityY < 0) { // roof possibility
            		centerY = sprite.getMaxY() + height / 2;
            		velocityY = -velocityY * BOUNCE_DAMPENING;
            	}

                if (Math.abs(velocityY) < MIN_VELOCITY_THRESHOLD && !keyboard.keyDown(38)) {
                    velocityY = 0;
                }
            }
            
            if (sprite instanceof StatusRemoverSprite && checkCollision(sprite)) {
            	removeStatusEffects();
            }
            
            if (sprite instanceof LevelEndSprite && checkCollision(sprite)) {
                levelComplete = true;
            }
            
            if (sprite instanceof FlappyBirdPortalSprite && checkCollision(sprite) && !flappyMode) {
                flappyMode = true;
            }
            if ((sprite instanceof WallSprite || sprite instanceof SpikeSprite) && checkCollision(sprite)) {
            	
            	if (CollisionDetection.pixelBasedOverlaps(this, sprite)) {
            		dispose = true;
            	}
            }
        }
    }

    private boolean checkCollision(DisplayableSprite sprite) {
        return !(sprite.getMaxX() < getMinX() ||
                 sprite.getMinX() > getMaxX() ||
                 sprite.getMaxY() < getMinY() ||
                 sprite.getMinY() > getMaxY());
    }
    
    private void removeStatusEffects() {
    	gravity = DEFAULT_GRAVITY;
    	currentImage = normalImage;
    	jetPower = DEFAULT_JET_POWER;
    	reversed = false;
    	flappyMode = false;
    	flapVelocity = -300;
    }
}
