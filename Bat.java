package caverunner;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Bat entity
 * @author Lord Byron's Army
 */
public class Bat extends Entity {
    
    String direction = "left";
    int imageDelay = 0;
    boolean hasStopped = false;
    int xDelay = 0;
    
    /**
     * Constructor
     * @param display
     * @param x
     * @param y
     * @param tileSize
     */
    public Bat(Display display, int x, int y, int tileSize) {
        super(display, tileSize, tileSize, x, y);
        scoreValue = health = 15;
        damage = 5;
        type = "bat";
        directoryPrefix = "entities/bat/";
    }
    
    /**
     * Updates the x coordinate
     */
    @Override
    public void updateX() {
        if (x > 0 && xDelay == 0) {
            x--;
            xDelay = 2;
        } else if (x <= 0) {
            disappear = true;
        }
        
        if (xDelay > 0) {
            xDelay--;
        }
    }
    
    /**
     * Updates the y coordinate
     */
    @Override
    public void updateY() {
        if (y > display.game.player.y + display.game.player.height / 2 && !hasStopped) {
            y -= 2;
        } else if (!hasStopped) {
            hasStopped = true;
        }
    }
    
    /**
     * Gets the image that best suits the current action
     * @return image
     */
    @Override
    public BufferedImage getImage() {
        if (imageDelay < 0) {
            imageDelay = 40;
        }
        
        imageDelay--;
        
        return display.game.batImages.get(display.game.batImageNames.indexOf(directoryPrefix + "flying_" + (imageDelay / 10 + 1) + "_" + direction + ".png"));
    }
    
}
