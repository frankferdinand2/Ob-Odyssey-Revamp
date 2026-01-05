import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ShellUniverse implements Universe {
    private boolean complete = false;    

    private ArrayList<DisplayableSprite> sprites = new ArrayList<>();
    private ArrayList<Background> backgrounds = new ArrayList<>();
    private ArrayList<DisplayableSprite> disposalList = new ArrayList<>();
    
    private double obSpeed;
    private double wallSpeed;
    private String currentLevelPath;
    private boolean resetLevel = false;
    private String[] levels = {"res/LevelData/level1.txt", "res/LevelData/level2.txt"};
    private int currentLevelIndex = 0;
    private boolean nextLevel = false;

    public ShellUniverse () {
        this.setXCenter(0);
        this.setYCenter(0);
        currentLevelPath = levels[0];

        if (!loadLevel(currentLevelPath)) {
            throw new RuntimeException("Level failed to load");
        }
    }
    
    public double getObSpeed() {
        return obSpeed;
    }

    public double getWallSpeed() {
        return wallSpeed;
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
        complete = true;
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
        for (int i = 0; i < sprites.size(); i++) {
            DisplayableSprite sprite = sprites.get(i);
            sprite.update(this, actual_delta_time);

            if (sprite instanceof ObSprite) {
                ObSprite ob = (ObSprite) sprite;
                if (ob.getLevelComplete()) {
                    nextLevel = true;
                    ob.setDispose(true);
                }
            }

            if (sprite instanceof ObSprite && sprite.getDispose()) {
                resetLevel = true;
            }

            if (resetLevel) {
                resetLevel();
            }

            if (nextLevel) {
                nextLevel();
                return;
            }
        }
        disposeSprites();
    }

    protected void disposeSprites() {
        for (int i = 0; i < sprites.size(); i++) {
            DisplayableSprite sprite = sprites.get(i);
            if (sprite.getDispose() == true) {
                disposalList.add(sprite);
            }
        }
        for (int i = 0; i < disposalList.size(); i++) {
            DisplayableSprite sprite = disposalList.get(i);
            sprites.remove(sprite);
        }
        if (disposalList.size() > 0) {
            disposalList.clear();
        }
    }

    public String toString() {
        return "ShellUniverse";
    }

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

                String[] tokens = line.split("\\s+"); // ignore spaces 

                if (tokens[0].equals("Ob")) {
                    System.out.println("Ob must not be defined in level file");
                    return false;
                }
                
                if (tokens[0].equals("ObSpeed")) {
                    obSpeed = Double.parseDouble(tokens[1]);
                    continue;
                }

                if (tokens[0].equals("WallSpeed")) {
                    wallSpeed = Double.parseDouble(tokens[1]);
                    continue;
                }
                
                DisplayableSprite sprite = parseSprite(tokens, lineNumber);
                
                if (sprite == null) return false;

                if (sprite instanceof LevelEndSprite) {
                    hasLevelEnd = true;
                }
                

                loadedSprites.add(sprite);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!hasLevelEnd) {
            System.err.println("Level load failed: No LevelEnding sprite found.");
            return false;
        }

        sprites.clear();
        sprites.addAll(loadedSprites);
        sprites.add(new JetpackSprite(-400,0));
        sprites.add(new ObSprite(-400, 0));
        return true;
    }

    private DisplayableSprite parseSprite(String[] t, int lineNumber) {
        try {
            String type = t[0];

            switch (type) {
                case "Spike":
                    if (t.length != 5) break;
                    return new SpikeSprite(Double.parseDouble(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3]), Double.parseDouble(t[4]));
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
                    return new LevelEndSprite(
                        Double.parseDouble(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3]), Double.parseDouble(t[4]));
                case "FlappyBirdPortal":
                    if (t.length != 5) break;
                    return new FlappyBirdPortalSprite(Double.parseDouble(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3]), Double.parseDouble(t[4]));
                    
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid number at line " + lineNumber);
            return null;
        }

        System.out.println("Invalid sprite definition at line " + lineNumber);
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

        if (currentLevelIndex >= levels.length) {
            complete = true;           
            return;
        }

        currentLevelPath = levels[currentLevelIndex];

        boolean loaded = loadLevel(currentLevelPath);
        
        if (!loaded) {
            throw new RuntimeException("Failed to load next level: " + currentLevelPath);
        }
    }
}
