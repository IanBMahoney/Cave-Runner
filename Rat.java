package caverunner;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Rat entity
 * @author max
 */
public class Rat extends Entity {
    
    String direction = "right";
    int xDelay = 0;
//int imageDelay = 0;
    
    /**
     * Constructor
     * @param display
     * @param x
     * @param y
     * @param tileSize
     */
    public Rat(Display display, int x, int y, int tileSize) {
        super(display, tileSize, tileSize, x, y);
        
        scoreValue = health = 20;
        damage = 3;
        type = "rat";
        directoryPrefix = "entities/rat/";
        loadImages();
    }
    
    /**
     * Updates the x coordinate
     */
    @Override
    public void updateX() {
        if (direction.equals("left") && hasTileOnLeft()) {
            direction = "right";
        } else if (direction.equals("right") && hasTileOnRight()) {
            direction = "left";
        }
        
        if (x == 0 && direction.equals("left")) {
            direction = "right";
        } else if (x == display.game.tiles.length * display.game.tileSize - width && direction.equals("right")) {
            direction = "left";
        }
        
        if (direction.equals("right")) {
            x++;
        } else {
            x--;
        }
    }
    
    /**
     * Gets the image that best suits the current action
     * @return image
     */
    @Override
    public BufferedImage getImage() {
        if (xDelay <= 0) {
            xDelay = 30;
        }
        
        xDelay--;
        
        return display.game.ratImages.get(display.game.ratImageNames.indexOf(directoryPrefix + "running_" + (xDelay / 10 + 1) + "_" + direction + ".png"));
    }
    
}
