import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ShellBackground implements Background {

    private int tileWidth;
    private int tileHeight;
    private double shiftX = 0;
    private double shiftY = 0;
    private BufferedImage bgImage;

    public ShellBackground(int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        bgImage = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bgImage.createGraphics();
        g.setColor(Color.BLACK); 
        g.fillRect(0, 0, tileWidth, tileHeight);
        g.dispose();
    }

    @Override
    public Tile getTile(int col, int row) {
        int x = col * tileWidth + (int) shiftX;
        int y = row * tileHeight + (int) shiftY;
        return new Tile(bgImage, x, y, tileWidth, tileHeight, false); 
    }

    @Override
    public int getCol(double x) {
        return (int) Math.floor((x - shiftX) / tileWidth);
    }

    @Override
    public int getRow(double y) {
        return (int) Math.floor((y - shiftY) / tileHeight);
    }

    @Override
    public double getShiftX() { 
    	return shiftX; 
	}

    @Override
    public double getShiftY() { 
    	return shiftY; 
	}

    @Override
    public void setShiftX(double shiftX) { 
    	this.shiftX = shiftX; 
	}

    @Override
    public void setShiftY(double shiftY) { 
    	this.shiftY = shiftY; 
	}

    @Override
    public void update(Universe universe, long actual_delta_time) {
        if (!(universe instanceof ShellUniverse)) return;
        ShellUniverse su = (ShellUniverse) universe;
        Color bgColour = su.getColour();

        if (!bgColour.equals(new Color(bgImage.getRGB(0, 0), true))) {
            Graphics2D g = bgImage.createGraphics();
            g.setColor(bgColour);
            g.fillRect(0, 0, tileWidth, tileHeight);
            g.dispose();
        }
    }
}
