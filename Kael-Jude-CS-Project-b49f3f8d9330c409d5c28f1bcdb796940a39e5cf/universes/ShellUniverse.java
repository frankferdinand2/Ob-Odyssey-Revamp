import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
public class ShellUniverse implements Universe {
   private boolean complete = false;   
   private ArrayList<DisplayableSprite> sprites = new ArrayList<>();
   private ArrayList<Background> backgrounds = new ArrayList<>();
   private ArrayList<DisplayableSprite> disposalList = new ArrayList<>();
   private ArrayList<DisplayableSprite> infiniteSprites = new ArrayList<>();
   private ArrayList<DisplayableSprite> pendingSprites = new ArrayList<>();
   private double lastSpawnX = 0; // infinite mode so spikes dont spawn on top eachother
   private double obSpeed;
   private double wallSpeed;
   private double jetpackBattery;
   private String currentLevelPath;
   private boolean resetLevel = false;
   private String[] levels = {"res/LevelData/level2.txt", "res/LevelData/level1.txt", "res/LevelData/level2.txt"};
   private int currentLevelIndex = 0;
   private boolean nextLevel = false;
   private boolean mainScreen = false;
   private boolean infiniteMode = false;
   private boolean flappy;
   private boolean reversed;
   private int lastPortal = 0;
   private String textOnScreen = "";
   private int attempts = 0;
   private int distance = 0;
   private int lightYears = 0;
   private int highScoreInfinite = 0;
   
   public ShellUniverse() {
       this.setXCenter(0);
       this.setYCenter(0);
       currentLevelPath = levels[0];
       if (!loadLevel(currentLevelPath)) {
           throw new RuntimeException("Level failed to load");
       }


   }
   
   public void setTextOnScreen(String textOnScreen) {
	   this.textOnScreen = textOnScreen;
   }
   
   public double getObSpeed() { 
	   return obSpeed; 
   }
   
   public double getWallSpeed() { 
	   return wallSpeed; 
   }
   
   public double getJetpackBattery() { 
	   return jetpackBattery; 
   }
   
   public double getScale() { 
	   return 1;
   }
   public double getXCenter() { 
	   return 0; 
   }
   
   public double getYCenter() { 
	   return 0; 
   }
   
   public void setXCenter(double xCenter) {
	   
   }
   public void setYCenter(double yCenter) {
	   
   }
   public boolean isComplete() { 
	   return complete; 
   }
   
   public void setComplete(boolean complete) { 
	   this.complete = true; 
   }
   
   public ArrayList<Background> getBackgrounds() { 
	   return backgrounds; 
   }
   
   public ArrayList<DisplayableSprite> getSprites() { 
	   return sprites; 
   }
   
   public boolean centerOnPlayer() { 
	   return false; 
   }
   
   public void update(Animation animation, long actual_delta_time) {
	   boolean changed = false;
       boolean obDiedInInfinite = false;
       for (int i = 0; i < sprites.size(); i++) {
           DisplayableSprite sprite = sprites.get(i);
           sprite.update(this, actual_delta_time);
           if (infiniteMode && sprite instanceof ObSprite && sprite.getDispose()) {
               obDiedInInfinite = true;
               if (lightYears > highScoreInfinite) {
            	   highScoreInfinite = lightYears;
               }
               distance = 0;
           }
           
           
           if (sprite instanceof SpikeSprite) {
        	   if (infiniteMode && !changed) {
            	   distance += -0.1 * (int) (actual_delta_time * 0.001 * ((SpikeSprite) sprite).getVelocityX());
            	   changed = true;
               }
           }
           if (mainScreen) {
        	   setTextOnScreen("");
           }
           if (infiniteMode) {
        	   lightYears = (int) (distance * 0.05);
        	   setTextOnScreen("Lightyears: " + lightYears);
           }
           if (sprite instanceof ObSprite) {
               ObSprite ob = (ObSprite) sprite;
               if (ob.getLevelComplete() && !infiniteMode) {
                   nextLevel = true;
                   ob.setDispose(true);
               }
               
               flappy = ob.getFlappyMode();
               reversed = ob.getReversed();
           }
           if (sprite instanceof HomeSprite && ((HomeSprite) sprite).isClicked()) {
               mainScreen = true;
               infiniteMode = false;
               distance = 0; 
               attempts = 0;
           }
           
           
           if (sprite instanceof ObSprite && sprite.getDispose() && !infiniteMode) {
        	   attempts ++;
               resetLevel = true;
               setTextOnScreen("attempts: " + attempts);
           }
           if (sprite instanceof PlaySprite && ((PlaySprite) sprite).isClicked()) {
               mainScreen = false;
               if (!infiniteMode) resetLevel();
           }
           if (sprite instanceof InfiniteButton && ((InfiniteButton) sprite).isClicked()) {
               startInfiniteMode();
           }
           
           if (nextLevel) {
        	   attempts = 0;
        	   
           }
       }
       
       if (resetLevel && !mainScreen && !infiniteMode) resetLevel();
       if (nextLevel && !mainScreen && !infiniteMode) nextLevel();
       if (mainScreen && !infiniteMode) mainScreen();
      
       KeyboardInput keyboard = KeyboardInput.getKeyboard();
       if (infiniteMode && !obDiedInInfinite) {
           // spawn based on distance
           SpikeSprite lastSpike = null;
           for (int i = infiniteSprites.size() - 1; i >= 0; i--) {
               if (infiniteSprites.get(i) instanceof SpikeSprite) {
                   lastSpike = (SpikeSprite) infiniteSprites.get(i);
                   break;
               }
           }
           if (lastSpike != null && lastSpike.getCenterX() - (-400) >= 600) {
               spawnInfiniteObstacle();
           }
       }
       sprites.addAll(pendingSprites);
       pendingSprites.clear();
       disposeSprites();

      
       if (obDiedInInfinite) {
           infiniteMode = false;
           resetInfiniteMode();
       }
   }
   protected void disposeSprites() {
	    for (DisplayableSprite sprite : sprites) {
	        if (sprite.getDispose()) {
	            disposalList.add(sprite);
	            infiniteSprites.remove(sprite); 
	        }
	    }
	    sprites.removeAll(disposalList);
	    disposalList.clear();
	}
   public String toString() { return "ShellUniverse"; }
   private boolean loadLevel(String path) {
       ArrayList<DisplayableSprite> loadedSprites = new ArrayList<>();
       boolean hasLevelEnd = false;
       try (BufferedReader br = new BufferedReader(new FileReader(path))) {
           String line;
           int lineNumber = 0;
           while ((line = br.readLine()) != null) {
               lineNumber++;
               line = line.trim();
               if (line.isEmpty() || line.startsWith("#")) continue;
               String[] tokens = line.split("\\s+");
               if (tokens[0].equals("Ob")) return false;
               if (tokens[0].equals("ObSpeed")) { obSpeed = Double.parseDouble(tokens[1]); continue; }
               if (tokens[0].equals("JetpackBattery")) { jetpackBattery = Double.parseDouble(tokens[1]); continue; }
               if (tokens[0].equals("WallSpeed")) { wallSpeed = Double.parseDouble(tokens[1]); continue; }
               DisplayableSprite sprite = parseSprite(tokens, lineNumber);
               if (sprite == null) return false;
               if (sprite instanceof LevelEndSprite) hasLevelEnd = true;
               loadedSprites.add(sprite);
           }
       } catch (IOException e) { e.printStackTrace(); return false; }
       if (!hasLevelEnd && !infiniteMode) return false;
       sprites.clear();
       sprites.addAll(loadedSprites);
       sprites.add(new JetpackSprite(-400,0));
       sprites.add(new ObSprite(-400,0));
       sprites.add(new HomeSprite(-600, -300));
       return true;
   }
   private DisplayableSprite parseSprite(String[] t, int lineNumber) {
       try {
           String type = t[0];
           switch (type) {
               case "Spike":
                   if (t.length < 5 || t.length > 6) break;
                   String imgPath = (t.length == 6 && !t[5].isEmpty()) ? t[5] : "res/red.jpg";
                   return new SpikeSprite(Double.parseDouble(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3]), Double.parseDouble(t[4]), imgPath);
               case "Floor":
                   if (t.length != 5) break;
                   return new FloorSprite(Double.parseDouble(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3]), Double.parseDouble(t[4]));
               case "Wall":
                   if (t.length != 3) break;
                   return new WallSprite(Double.parseDouble(t[1]), Double.parseDouble(t[2]));
               case "StatusRemover":
                   if (t.length != 3) break;
                   return new StatusRemoverSprite(Double.parseDouble(t[1]), Double.parseDouble(t[2]));
               case "ReverseGravityPortal":
                   if (t.length != 3) break;
                   return new ReverseGravityPortalSprite(Double.parseDouble(t[1]), Double.parseDouble(t[2]));
               case "LevelEnding":
                   if (t.length != 5) break;
                   return new LevelEndSprite(Double.parseDouble(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3]), Double.parseDouble(t[4]));
               case "FlappyBirdPortal":
                   if (t.length != 5) break;
                   return new FlappyBirdPortalSprite(Double.parseDouble(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3]), Double.parseDouble(t[4]));
           }
       } catch (NumberFormatException e) { return null; }
       return null;
   }
   private void resetLevel() {
       resetLevel = false;
       sprites.clear();
       loadLevel(currentLevelPath);
   }
   private void nextLevel() {
       nextLevel = false;
       sprites.clear();
       currentLevelIndex++;
       if (currentLevelIndex >= levels.length) { complete = true; return; }
       currentLevelPath = levels[currentLevelIndex];
       if (!loadLevel(currentLevelPath)) throw new RuntimeException("Failed to load next level: " + currentLevelPath);
   }
   private void mainScreen() {
       sprites.clear();
       sprites.add(new PlaySprite(0,200));
       sprites.add(new InfiniteButton(0, -200));
   }
   private void startInfiniteMode() {
       infiniteMode = true;
       mainScreen = false;
       obSpeed = 600;
       resetLevel = false;
       nextLevel = false;
       sprites.clear();
       ObSprite ob = new ObSprite(-400, 0);
       JetpackSprite jetpack = new JetpackSprite(-400, 0);
       HomeSprite home = new HomeSprite(-600, -300);
       sprites.add(jetpack);
       sprites.add(ob);
       sprites.add(home);
       infiniteSprites.clear();
       jetpackBattery = 1e10;
       // Spawn starter spike to track ob speed (more importantly ob distance)
       SpikeSprite starterSpike = new SpikeSprite(1200, 0, 200, 200, "res/SpriteImages/SpikeImages/Box200x200.png");
       sprites.add(starterSpike);
       infiniteSprites.add(starterSpike);
       lastSpawnX = starterSpike.getCenterX();
   }
   private void spawnInfiniteObstacle() {
       Random rand = new Random();
       int count = 1 + rand.nextInt(3);
       for (int i = 0; i < count; i++) {
           double x = lastSpawnX + 600 + i * 250;
           double y = 0 + rand.nextDouble() * 400;
           if (rand.nextInt(2) == 0) {
        	   y = y * -1;
           }
           double width = 200;
           double height = 200;
           if (rand.nextInt(10) == 2) {
        	   String img = "res/SpriteImages/SpikeImages/Box200x200.png";
        	   SpikeSprite spike = new SpikeSprite(x+200, y, width, height, img);
        	   SpikeSprite spikeB = new SpikeSprite(x-200, y, width, height, img);

        	   FloorSprite floor = new FloorSprite(x, y, width, height + 5);
        	   pendingSprites.add(spike);
        	   pendingSprites.add(spikeB);
        	   pendingSprites.add(floor);
        	   infiniteSprites.add(floor);
        	   infiniteSprites.add(spike);
        	   infiniteSprites.add(spikeB);
               
               lastPortal++;
           }
           else if (rand.nextInt(6) == 2) {
        	   String img = "res/SpriteImages/SpikeImages/Plant200x400.png";
        	   SpikeSprite spike = new SpikeSprite(x, 140, 200, 400, img);
        	   pendingSprites.add(spike);
               infiniteSprites.add(spike);
               lastPortal++;

           }
           else if (rand.nextInt(9) == 1) {
        	   SpikeSprite spikeA = new SpikeSprite(x, -260, 200, 200, "res/SpriteImages/SpikeImages/Box200x200.png");
        	   SpikeSprite spikeB = new SpikeSprite(x - 200, 270, 100, 100, "res/SpriteImages/SpikeImages/Box100x100.png");
        	   SpikeSprite spikeC = new SpikeSprite(x-50, 220, 200, 200, "res/SpriteImages/SpikeImages/Box200x200.png");
        	   SpikeSprite spikeD = new SpikeSprite(x, 70, 100, 100, "res/SpriteImages/SpikeImages/Box100x100.png");
        	   pendingSprites.add(spikeA);
        	   pendingSprites.add(spikeB);
        	   pendingSprites.add(spikeC);
        	   pendingSprites.add(spikeD);
        	   infiniteSprites.add(spikeA);
        	   infiniteSprites.add(spikeB);
        	   infiniteSprites.add(spikeC);
        	   infiniteSprites.add(spikeD);
        	   lastPortal++;
           }
           else if (rand.nextInt(10) == 4 && lastPortal > 15) { // small chance at portal spawn
               DisplayableSprite portal = null;
               ArrayList<DisplayableSprite> possible = new ArrayList<>();
               x += 250;

               if (!reversed)
                   possible.add(new ReverseGravityPortalSprite(x, 0));

               if (!flappy)
                   possible.add(new FlappyBirdPortalSprite(x, 0, width, height));
               
               if (flappy || reversed) {
            	   possible.add(new StatusRemoverSprite(x, 0));
               }
               portal = possible.get(rand.nextInt(possible.size()));
               if (rand.nextInt(2) == 0) {
            	   SpikeSprite spikeA = new SpikeSprite(x, 250, width, height, "res/SpriteImages/SpikeImages/Box200x200.png");
                   SpikeSprite spikeB = new SpikeSprite(x, -250, width, height, "res/SpriteImages/SpikeImages/Box200x200.png");
                   pendingSprites.add(spikeA);
                   pendingSprites.add(spikeB);
                   infiniteSprites.add(spikeA);
                   infiniteSprites.add(spikeB);
               }
               else {
            	   SpikeSprite spikeA = new SpikeSprite(x-200, 250, width, height, "res/SpriteImages/SpikeImages/Box200x200.png");
                   SpikeSprite spikeB = new SpikeSprite(x+200, -250, width, height, "res/SpriteImages/SpikeImages/Box200x200.png");
                   SpikeSprite spikeC = new SpikeSprite(x+200, 250, width, height, "res/SpriteImages/SpikeImages/Box200x200.png");
                   SpikeSprite spikeD = new SpikeSprite(x-200, -250, width, height, "res/SpriteImages/SpikeImages/Box200x200.png");

                   FloorSprite floorA = new FloorSprite(x, 250, 200, 205);
                   FloorSprite floorB = new FloorSprite(x, -250, 200, 205);
                   pendingSprites.add(spikeA);
                   pendingSprites.add(spikeB);
                   pendingSprites.add(spikeC);
                   pendingSprites.add(spikeD);
                   pendingSprites.add(floorA);
                   pendingSprites.add(floorB);
                   infiniteSprites.add(floorA);
                   infiniteSprites.add(floorB);
                   infiniteSprites.add(spikeA);
                   infiniteSprites.add(spikeB);
                   infiniteSprites.add(spikeC);
                   infiniteSprites.add(spikeD);
                   
               }
               
               pendingSprites.add(portal);

               infiniteSprites.add(portal);
               lastPortal = 0;
               
           } else { // spawn spikes
               String img = "res/SpriteImages/SpikeImages/Box200x200.png";
               SpikeSprite spike = new SpikeSprite(x, y, width, height, img);
               pendingSprites.add(spike);
               infiniteSprites.add(spike);
               lastPortal++;
           }
           lastSpawnX = x;
       }
   }
   
   public String getTextOnScreen() {
	   return textOnScreen;
   }
   private void resetInfiniteMode() {
       for (DisplayableSprite sprite : sprites) sprite.setDispose(true);
       disposeSprites();
       mainScreen = true;
       infiniteMode = false;
       mainScreen();
   }
}
