package caverunner;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Player entity
 *
 * @author max
 */
public class Player extends Entity {

    ArrayList<BufferedImage> shadowImages;
    BufferedImage tutorialImage;
    BufferedImage healthImage;
    ArrayList<BufferedImage> healthbarImages;
    String direction = "right";
    int xDelay = 0;
    int jumpDistance = 0;
    int jumpMax = 4;
    int xMomentum = 0;
    int yMomentum = 0;
    //boolean moving = false; // add booleans for direction so it can change if you are still holding down one direction and let go of another
    boolean leftButton = false;
    boolean rightButton = false;
    //boolean jump = false;
    boolean jumping = false;
    boolean justJumped = false;
    boolean jumpButton = false;

    String currentWeapon = "lanturn";
    int attackDelay = 0;
    int lanturnLevel = 1;
    int damageDelay = 0;
    boolean currentlyDamaged = false;

    /*
    Use the value from delay to determine which running animation is being used
     */
    /**
     * Constructor
     *
     * @param display
     * @param x
     * @param y
     * @param tileSize
     */
    public Player(Display display, int x, int y, int tileSize) {
        super(display, tileSize, tileSize * 2, x, y);
        jumpMax *= tileSize;
        directoryPrefix = "entities/player/";
        health = 100;
        loadImages();
    }

    /**
     * Runs whenever an attack is used and the player is able to
     *
     * @param type
     */
    public void attack(String type) {
        if (attackDelay <= 0) {
            attackDelay = 40;
            lanternAttack();

            display.log("Player attacked");
        }
    }

    /**
     * Checks for collisions with any other entity and deals with it accordingly
     */
    public void checkForCollisions() {
        for (int i = 0; i < display.game.entities.size(); i++) {
            Entity entity = display.game.entities.get(i);

            if ((x >= entity.x || x + width >= entity.x) && (x <= entity.x + entity.width || x + width <= entity.x + entity.width)
                    && (y >= entity.y || y + height >= entity.y) && (y <= entity.y + entity.height || y + height <= entity.y + entity.height)) {
                if (entity.safe) {
                    if (entity.type.equals("health")) {
                        health = 100;
                        display.game.entities.remove(i);
                        i--;
                        continue;
                    }
                }

                if (currentlyDamaged) {
                    return;
                }

                damageDelay = 90;
                health -= entity.damage;
                currentlyDamaged = true;
                display.playMusic("hurt_sound", false);
                
                if (health <= 0) {
                    health = 0;
                    display.game.gameOver();
                }
            }
        }
    }

    /**
     * Updates the health status
     */
    public void updateHealth() {
        if (damageDelay > 0) {
            damageDelay--;
            currentlyDamaged = true;
        } else {
            currentlyDamaged = false;
        }
    }

    /**
     * Runs when a lantern attack is used
     */
    public void lanternAttack() {
        int attackDistance = (int) (display.game.tileSize * 1.5);
        int attackDamage = 14 * lanturnLevel;
        int timesRun = 0;
        
        if (direction.equals("left")) {
            System.out.println("melee ran");
            for (int i = 0; i < display.game.entities.size(); i++) {
                Entity entity = display.game.entities.get(i);

                if (!safe && entity.x + entity.width >= x - attackDistance && entity.x + entity.width < x
                        && (y + height >= entity.y && entity.y + entity.height >= y)) {
                    timesRun++;
                    
                    if (timesRun == 1) {
                        display.playMusic("melee_sound", false);
                    }
                    
                    i = dealDamageToEntity(i, attackDamage);
                }
            }
        } else {
            System.out.println("melee ran");
            for (int i = 0; i < display.game.entities.size(); i++) {
                Entity entity = display.game.entities.get(i);

                if (!safe && entity.x >= x + width && entity.x < x + width + attackDistance
                        && (y + height >= entity.y && entity.y + entity.height >= y)) {
                    timesRun++;
                    
                    if (timesRun == 1) {
                        display.playMusic("melee_sound", false);
                    }
                    
                    i = dealDamageToEntity(i, attackDamage);
                }
            }
        }
    }

    /**
     * Deals a certain amount of damage to an entity
     *
     * @param entity
     * @param attackDamage
     * @return index to continue the search at
     */
    public int dealDamageToEntity(int entity, int attackDamage) {
        display.game.entities.get(entity).health -= attackDamage;

        if (display.game.entities.get(entity).health < 0) {
            display.game.score += display.game.entities.get(entity).scoreValue;
            display.game.entities.remove(entity);
            entity--;
        }

        return entity;
    }

    /**
     * Updates the x coordinate
     */
    @Override
    public void updateX() {
        int startingX = x;

        if (xDelay == 0 && (leftButton || rightButton)) {
            xDelay = 45;
            xMomentum++;

            if (xMomentum > 2) {
                xMomentum = 2;
            }
        }

        if (leftButton || rightButton) {
            for (int i = 0; i < xMomentum; i++) {
                if (isMovingLeft() && x <= 0) {
                    xMomentum = 0;
                    xDelay = 0;
                } else if (isMovingRight() && x == display.game.tiles.length * display.game.tileSize - width) {
                    xMomentum = 0;
                    xDelay = 0;
                }

                if (isMovingLeft() && x > 0 && !hasTileOnLeft()) {
                    x--;
                } else if (isMovingRight() && x < display.game.tiles.length * display.game.tileSize - width && !hasTileOnRight()) {
                    x++;
                }
            }
        }

        if (x == startingX) {
            xMomentum = 0;
            xDelay = 0;
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
        int amount = 2;

        if ((!falling && !jumping && !onGround()) || (hasTileAbove())) {
            falling = true;
        }

        if (yDelay == 0) {
            yDelay = 1;

            if (falling) {
                jumping = false;
                for (int i = 0; i < amount; i++) {
                    if (onGround()) {
                        falling = false;
                        jumpDistance = 0;
                    } else {
                        y--;
                    }
                }
            } else if (jumping) {
                for (int i = 0; i < amount; i++) {
                    if (y + 1 < display.game.tiles[0].length * 16) {
                        y++;
                        jumpDistance++;
                    } else {
                        y--;
                        jumpDistance = jumpMax;
                    }

                    if (jumpDistance >= jumpMax) {
                        falling = true;
                    }
                }
            }
        }

        if (yDelay > 0) {
            yDelay--;
        }

        if (!falling) {
            justJumped = false;
        }
    }

    /**
     * Checks to see if there is a solid tile above
     *
     * @return true if a solid tile is above
     */
    public boolean hasTileAbove() { // change this to be compatible with all heights, then move to entity
        try {
            if (display.game.tiles[x / display.game.tileSize][y / display.game.tileSize + height / display.game.tileSize].b == 1 && x % display.game.tileSize < 14) {
                return true;
            } else if (display.game.tiles[x / display.game.tileSize + 1][y / display.game.tileSize + height / display.game.tileSize].b == 1 && x % display.game.tileSize > 2) {
                return true;
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
     * Checks to see if the player is moving left
     *
     * @return true if the player is moving left
     */
    public boolean isMovingLeft() {
        return leftButton && direction.equals("left");
    }

    /**
     * Checks to see if the player is moving right
     *
     * @return true if the player is moving right
     */
    public boolean isMovingRight() {
        return rightButton && direction.equals("right");
    }

    public BufferedImage getShadowImage() {
        if (attackDelay <= 0) {
            return shadowImages.get(0);
        } else {
            return shadowImages.get(1);
        }
    }

    /**
     * Loads images associated with the entity
     */
    @Override
    public void loadImages() {// make this code and the getImage more condensed
        shadowImages = new ArrayList();
        shadowImages.add(display.loadImage("shadow.png"));
        shadowImages.add(display.loadImage("shadow_red.png"));

        tutorialImage = display.loadImage("tutorial.png");
        healthImage = display.loadImage("health/health.png");

        healthbarImages = new ArrayList();

        for (int i = 0; i < 37; i++) {
            healthbarImages.add(display.loadImage("health/" + i + ".png"));
        }

        images = new ArrayList();
        imageNames = new ArrayList();

        imageNames.add(directoryPrefix + "standing_right.png");
        imageNames.add(directoryPrefix + "standing_left.png");

        imageNames.add(directoryPrefix + "jumping_right.png");
        imageNames.add(directoryPrefix + "jumping_left.png");
        
        for (int i = 0; i < 10; i++) {
            imageNames.add(directoryPrefix + "running_" + (i + 1) + "_right.png");
            imageNames.add(directoryPrefix + "running_" + (i + 1) + "_left.png");
        }

        for (int i = 0; i < imageNames.size(); i++) {
            images.add(display.loadImage(imageNames.get(i)));
        }
    }

    /**
     * Gets the image that best suits the current action ALSO INCLUDES
     * ATTACKDELAY UPDATE
     *
     * @return BufferedImage containing the proper image
     */
    @Override
    public BufferedImage getImage() {
        if (attackDelay > 0) {
            attackDelay--;
        }

        if (direction.equals("right")) {
            if (jumping || falling) {
                return images.get(imageNames.indexOf(directoryPrefix + "jumping_right.png"));
            } else if (isMovingRight() && x + width < display.game.tiles.length * display.game.tileSize && !hasTileOnRight()) {
                return images.get(imageNames.indexOf(directoryPrefix + "running_" + (10 - xDelay / 5) + "_right.png")); // 10 is the amount of frames in delay
            }

            return images.get(imageNames.indexOf(directoryPrefix + "standing_right.png"));
        } else {
            if (jumping || falling) {
                return images.get(imageNames.indexOf(directoryPrefix + "jumping_left.png"));
            } if (isMovingLeft() && x != 0 && !hasTileOnLeft()) {
                return images.get(imageNames.indexOf(directoryPrefix + "running_" + (10 - xDelay / 5) + "_left.png"));
            }

            return images.get(imageNames.indexOf(directoryPrefix + "standing_left.png"));
        }
    }

}
