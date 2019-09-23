package caverunner;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * The core class used with every entity
 * @author max
 */
public class Entity {

    Display display;
    String directoryPrefix;
    ArrayList<BufferedImage> images;
    ArrayList<String> imageNames;
    int x, y;
    int width, height;
    boolean falling = false;
    int yDelay = 0;
    int health = 1;
    int damage = 1;
    boolean safe = false;
    boolean enabled = false;
    boolean disappear = false;
    String type;
    int scoreValue;

    /**
     * Constructor
     * @param display
     * @param width
     * @param height
     * @param x
     * @param y 
     */
    public Entity(Display display, int width, int height, int x, int y) {
        this.display = display;
        this.width = width;
        this.height = height;
        //setX(x);
        //setY(y);
        this.x = x;
        this.y = y;
        display.log("Entity spawned");
    }

    /**
     * Loads images associated with the entity -- empty as it needs to be overridden in every class
     */
    public void loadImages() {

    }

    /**
     * Gets the image that best suits the current action -- empty as it needs to be overridden in every class
     * @return image
     */
    public BufferedImage getImage() {
        return null;
    }

    /**
     * Updates the x coordinate -- empty as it needs to be overridden in every class
     */
    public void updateX() {

    }

    /**
     * Updates the y coordinate
     */
    public void updateY() {
        int amount = 2;

        if ((!falling && !onGround())) {
            falling = true;
        }

        if (yDelay == 0) {
            yDelay = 1;

            if (falling) {
                for (int i = 0; i < amount; i++) {
                    if (onGround()) {
                        falling = false;
                    } else {
                        y--;
                    }
                }
            }
        }

        if (yDelay > 0) {
            yDelay--;
        }
    }

    /**
     * Checks to see if there is a solid tile below
     *
     * @return true if a solid tile is below
     */
    public boolean onGround() {
        //System.out.println((x / 16) + " " + (y / 16) + " " + display.game.tiles[x / 16][y / 16].b);
        try {
            if (display.game.tiles[x / display.game.tileSize][y / display.game.tileSize - 1].b == 1 && y % 16 == 0) {
                return true;
            } else if (display.game.tiles[(x + width - 1) / display.game.tileSize][y / display.game.tileSize - 1].b == 1 && y % 16 == 0) {
                return true;
            }

            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (Exception e) {
            display.handleException(e, "Problem calculating if an entity is on the ground");
            return false;
        }
    }

    /**
     * Checks to see if the entity is currently being painted on the screen
     * @return true if it is on the screen
     */
    public boolean onScreen() {
        if (display.game.xOffset < x && x < display.game.xOffset + display.game.tileSize * 16 && 
                display.game.yOffset < y && y < display.game.yOffset + display.game.tileSize * 9) {
            return true;
        }
        
        return false;
    }

    /**
     * Checks to see if there is a solid tile to the left
     *
     * @return true if a solid tile is to the left
     */
    public boolean hasTileOnLeft() {
        try {
            int extraHeight = 0;
            
            if (height % display.game.tileSize != 0) {
                extraHeight = 16;
            }
            
            for (int i = 0; i < (height + extraHeight) / display.game.tileSize; i++) {
                if (display.game.tiles[(x - 1) / display.game.tileSize][y / display.game.tileSize + i].b == 1) {
                    return true;
                }
            }

            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (Exception e) {
            display.handleException(e, "Unknown cause");
            return false;
        }
    }

    /**
     * Checks to see if there is a solid tile to the right
     *
     * @return true if a solid tile is to the right
     */
    public boolean hasTileOnRight() {
        try {
            int extraHeight = 0;
            
            if (height % display.game.tileSize != 0) {
                extraHeight = 16;
            }
            
            for (int i = 0; i < (height + extraHeight) / display.game.tileSize; i++) {
                if (display.game.tiles[x / display.game.tileSize + width / display.game.tileSize][y / display.game.tileSize + i].b == 1) {
                    return true;
                }
            }

            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        } catch (Exception e) {
            display.handleException(e, "Unknown cause");
            return false;
        }
    }

}
