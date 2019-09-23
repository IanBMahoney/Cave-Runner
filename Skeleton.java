package caverunner;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Maikelele
 */
public class Skeleton extends Entity {
    
    String direction = "left";
    int imageDelay = 0;
    
    public Skeleton(Display display, int x, int y, int tileSize) {
        super(display, tileSize, tileSize * 2, x, y);
        directoryPrefix = "entities/skeleton/";
        scoreValue = health = 35;
        damage = 8;
        type = "skeleton";
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
        if (imageDelay <= 0) {
            imageDelay = 40;
        }
        
        imageDelay--;
        //System.out.println(imageDelay + "\t" + (imageDelay / 10 + 1));
        return display.game.skeletonImages.get(display.game.skeletonImageNames.indexOf(directoryPrefix + "running_" + (imageDelay / 10 + 1) + "_" + direction + ".png"));
    }
    
}
