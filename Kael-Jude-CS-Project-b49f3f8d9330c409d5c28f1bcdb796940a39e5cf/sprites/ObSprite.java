import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ObSprite implements DisplayableSprite {

    private static final String IMAGE_PATH = "res/SpriteImages/ObSprite.png";
    private static final AudioPlayer SOUND_FX = new AudioPlayer();
    private static final double DEFAULT_WIDTH = 85.0;
    private static final double DEFAULT_HEIGHT = 85.0;
    private static final double GROUND_Y = 360.0 - 35.0;
    private static final double BOUNCE_DAMPENING = 0.8;
    private static final double ROOF_Y = -360;
    private static final double MIN_VELOCITY_THRESHOLD = 10.0;
    private static final double DEFAULT_JET_POWER = -1000;
    private static final double DEFAULT_GRAVITY = 450;
    
    private boolean imageSynced = false;
    private boolean flappyMode = false;
    private double flapVelocity = -300;
    private double jetBattery = 2000;
    private boolean loadFrame = false;
    private boolean wasOnGround = false;
    private boolean wasOnRoof = false;
    private static Image normalImage;
    private double originalJetpackBattery;

    private Image baseImage;    
    private Image currentImage;
    private boolean visible = true;
    private double gravity;
    private double centerX;
    private double centerY;
    private double width;
    private double height;
    private boolean dispose;
    private double velocityY;
    private double jetPower;
    private boolean reversed = false;
    private boolean levelComplete = false;
    private boolean happened = false;
    private boolean found = false;
    private boolean invincible = false;
    private boolean boing = false;

    public ObSprite(double centerX, double centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.gravity = DEFAULT_GRAVITY;
        this.jetPower = DEFAULT_JET_POWER;

        try {
            if (normalImage == null) {
                normalImage = ImageIO.read(new File(IMAGE_PATH));
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + e);
        }

        baseImage = normalImage;     
        currentImage = baseImage;
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
    	return visible; 
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
    
    public double getJetRatio() {
    	return (jetBattery/originalJetpackBattery) * 100;
    }

    public void update(Universe universe, long actualDeltaTime) {
        double deltaTime = actualDeltaTime * 0.001;
        ShellUniverse u = (ShellUniverse) universe;
        String shellPath = u.getObImagePath();

        boing = "res/SpriteImages/goofychicken.png".equals(shellPath);

        if (loadFrame) {
        	
        	visible = true;
        }
        loadFrame = false;
        
        originalJetpackBattery = u.getJetpackBattery();
        if (!found) {
            jetBattery = u.getJetpackBattery();
            found = true;
        }
        
        if (!imageSynced) {
            Image img = normalImage;
            

            if (shellPath != null && !shellPath.equals(IMAGE_PATH)) {
                try {
                    img = ImageIO.read(new File(shellPath));
                } catch (IOException e) {
                    System.err.println("Error loading shell image: " + e);
                }
            }

            baseImage = img;
            currentImage = reversed ? ImageRotator.rotate(baseImage, 270) : baseImage;

            imageSynced = true;
        }

        KeyboardInput keyboard = KeyboardInput.getKeyboard();
        boolean jetActive = keyboard.keyDown(38) && jetBattery > 0;

        if (jetActive && !flappyMode) {
            velocityY += jetPower * deltaTime;
            jetBattery -= deltaTime;
            if (jetBattery < 0) jetBattery = 0;
        }

        if (flappyMode && keyboard.keyDownOnce(38)) {
            velocityY = flapVelocity;
            jetBattery -= 0.1;
        }
        
        if (keyboard.keyDown(78)) {
            invincible = true;
        }
        velocityY += gravity * deltaTime;
        centerY += velocityY * deltaTime;

        boolean onGroundNow = centerY + (height / 2) >= GROUND_Y;

        if (onGroundNow && velocityY > 0) {
            centerY = GROUND_Y - (height / 2);
            velocityY = -velocityY * BOUNCE_DAMPENING;

            if (boing && !reversed && Math.abs(velocityY) > 50) {
                SOUND_FX.setStop(true);
                SOUND_FX.playAsynchronous("res/boing.wav");
            }
        }


        wasOnGround = onGroundNow;

        boolean onRoofNow = centerY - (height / 2) <= ROOF_Y;

        if (onRoofNow && velocityY < 0) {
            centerY = ROOF_Y + (height / 2);
            velocityY = -velocityY * BOUNCE_DAMPENING;

            if (boing && reversed && Math.abs(velocityY) > 50) {
                SOUND_FX.setStop(true);
                SOUND_FX.playAsynchronous("res/boing.wav");
            }
        }

        
        wasOnRoof = onRoofNow;

        for (DisplayableSprite sprite : universe.getSprites()) {

            if (sprite instanceof ReverseGravityPortalSprite && checkCollision(sprite) && !reversed) {
                reversed = true;
                flapVelocity = 300;
                gravity = -DEFAULT_GRAVITY;
                jetPower = -DEFAULT_JET_POWER;

                if (!happened) {
                    currentImage = ImageRotator.rotate(baseImage, 270); 
                    happened = true;
                }
            }

            if (sprite instanceof JetBatteryPortal && checkCollision(sprite)
                    && !((JetBatteryPortal) sprite).getCollide()) {
                jetBattery = originalJetpackBattery;
                ((JetBatteryPortal) sprite).setCollide(true);
                ((JetBatteryPortal) sprite).setDispose(true);
            }

            if (sprite instanceof FloorSprite && checkCollision(sprite)) {

                if (velocityY > 0) {
                    centerY = sprite.getMinY() - height / 2;
                    velocityY = -velocityY * BOUNCE_DAMPENING;

                    if (boing && Math.abs(velocityY) > 50) {
                        SOUND_FX.setStop(true);
                        SOUND_FX.playAsynchronous("res/boing.wav");
                    }
                }
                else if (velocityY < 0) {
                    centerY = sprite.getMaxY() + height / 2;
                    velocityY = -velocityY * BOUNCE_DAMPENING;

                    if (boing && Math.abs(velocityY) > 50) {
                        SOUND_FX.setStop(true);
                        SOUND_FX.playAsynchronous("res/boing.wav");
                    }
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

            if ((sprite instanceof WallSprite || sprite instanceof SpikeSprite) && checkCollision(sprite) && !invincible) {
                if (CollisionDetection.pixelBasedOverlaps(this, sprite)) {
                    dispose = true;
                }
            }
        }
    }

    private boolean checkCollision(DisplayableSprite sprite) {
        return !(sprite.getMaxX() < getMinX()
                || sprite.getMinX() > getMaxX()
                || sprite.getMaxY() < getMinY()
                || sprite.getMinY() > getMaxY());
    }

    private void removeStatusEffects() {
        gravity = DEFAULT_GRAVITY;
        jetPower = DEFAULT_JET_POWER;
        reversed = false;
        flappyMode = false;
        flapVelocity = -300;
        happened = false;

        baseImage = normalImage;
        currentImage = baseImage;
    }
}
