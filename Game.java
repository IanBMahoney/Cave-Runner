package caverunner;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Class that contains all information regarding core game play
 * @author Lord Byron's Army
 */
public class Game {

    Display display;
    int level;

    Tile[][] tiles;
    ArrayList<BufferedImage> usedTileImages;
    ArrayList usedTileIds;
    int tileSize = 16;
    boolean tutorial = true;

    Player player;
    ArrayList<Entity> entities;

    int xOffset = 0;
    int yOffset = 0;
    boolean update = false;
    int score = 0;
    
    ArrayList<BufferedImage> batImages;
    ArrayList<String> batImageNames;
    ArrayList<BufferedImage> ratImages;
    ArrayList<String> ratImageNames;
    ArrayList<BufferedImage> skeletonImages;
    ArrayList<String> skeletonImageNames;
    
    
    /*
    should create a class for title and option screens
    have booleans in game to tell if the game should be printing or title/options menu
    make everything that should be saved inside of display
     */
    /**
     * Constructor
     * @param display
     * @param level 
     */
    public Game(Display display, int level) {
        this.display = display;
        this.level = level;
        readTiles();
        int[] playerCoordinates = readEntities();
        
        player = new Player(display, playerCoordinates[0], playerCoordinates[1], tileSize);
        display.log("Game created");
        update = true;
    }
    
    public void loadBatImages() {
        batImages = new ArrayList();
        batImageNames = new ArrayList();
        
        batImageNames.add("entities/bat/flying_1_left.png");
        batImageNames.add("entities/bat/flying_2_left.png");
        batImageNames.add("entities/bat/flying_3_left.png");
        batImageNames.add("entities/bat/flying_4_left.png");
        
        batImageNames.add("entities/bat/flying_1_right.png");
        batImageNames.add("entities/bat/flying_2_right.png");
        batImageNames.add("entities/bat/flying_3_right.png");
        batImageNames.add("entities/bat/flying_4_right.png");
        
        for (int i = 0; i < batImageNames.size(); i++) {
            
            batImages.add(display.loadImage(batImageNames.get(i)));
        }
    }
    
    /**
     * Loads images associated with the entity
     */
    public void loadRatImages() {
        ratImages = new ArrayList();
        ratImageNames = new ArrayList();
        
        ratImageNames.add("entities/rat/running_1_left.png");
        ratImageNames.add("entities/rat/running_2_left.png");
        ratImageNames.add("entities/rat/running_3_left.png");
        ratImageNames.add("entities/rat/running_1_right.png");
        ratImageNames.add("entities/rat/running_2_right.png");
        ratImageNames.add("entities/rat/running_3_right.png");
        
        
        for (int i = 0; i < ratImageNames.size(); i++) {
            ratImages.add(display.loadImage(ratImageNames.get(i)));
        }
    }
    
    /**
     * Loads images associated with the entity
     */
    public void loadSkeletonImages() {
        skeletonImages = new ArrayList();
        skeletonImageNames = new ArrayList();
        
        skeletonImageNames.add("entities/skeleton/running_1_left.png");
        skeletonImageNames.add("entities/skeleton/running_2_left.png");
        skeletonImageNames.add("entities/skeleton/running_3_left.png");
        skeletonImageNames.add("entities/skeleton/running_4_left.png");
        skeletonImageNames.add("entities/skeleton/running_1_right.png");
        skeletonImageNames.add("entities/skeleton/running_2_right.png");
        skeletonImageNames.add("entities/skeleton/running_3_right.png");
        skeletonImageNames.add("entities/skeleton/running_4_right.png");
        
        
        for (int i = 0; i < skeletonImageNames.size(); i++) {
            skeletonImages.add(display.loadImage(skeletonImageNames.get(i)));
        }
    }
    
    /**
     * Game over sequence
     */
    public void gameOver() {
        display.highScores.add("" + score);
        display.highScores.sort(Comparator.comparingInt(Integer::parseInt));
        display.highScores = display.reverseScores(display.highScores);
        display.writeToFile("high_scores.txt", display.highScores, true);
        display.currentClass = "title";
        display.title = new Title(display);
    }
    
    /**
     * Gets the image needed from any tile
     * @param tile
     * @return image
     */
    public BufferedImage getImageFromTile(Tile tile) {
        if (usedTileIds.contains(tile.r)) {
            return usedTileImages.get(usedTileIds.indexOf(tile.r));
        }

        return null;
    }
    
    /**
     * Reads the entities from an image
     * @return player x and y
     */
    public int[] readEntities() {
        int[] playerCoordinates = {0, 0};
        entities = new ArrayList();
        loadBatImages();
        loadRatImages();
        loadSkeletonImages();
        
        try {
            BufferedImage entitiesImage = display.loadImage("levels/" + level + "_entities.png");
            
            for (int i = 0; i < tiles.length; i++) {
                for (int j = 0; j < tiles[i].length; j++) {
                    Color color = new Color(entitiesImage.getRGB(i, j));
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();

                    if (r == 255 && g == 255 && b == 255) {
                        playerCoordinates[0] = i * tileSize;
                        playerCoordinates[1] = (tiles[i].length - 1 - j) * tileSize;
                    } else if (r == 1) {
                        entities.add(new Rat(display, i * tileSize, (tiles[i].length - 1 - j) * tileSize, tileSize));
                    } else if (r == 2) {
                        entities.add(new Bat(display, i * tileSize, (tiles[i].length - 1 - j) * tileSize, tileSize));
                    } else if (r == 3) {
                        entities.add(new Skeleton(display, i * tileSize, (tiles[i].length - 1 - j) * tileSize, tileSize));
                    } else if (r == 4) {
                        entities.add(new HealthPack(display, i * tileSize, (tiles[i].length - 1 - j) * tileSize, tileSize));
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            display.handleException(e, "Sprite image is not found");
        } catch (Exception e) {
            display.handleException(e, "Unknown cause");
        }

        display.log("Entities image read, " + entities.size() + " entities created");
        return playerCoordinates;
    }

    /**
     * Reads the tiles from an image
     */
    public void readTiles() {
        usedTileImages = new ArrayList();
        usedTileIds = new ArrayList();

        try {
            BufferedImage backgroundImage = display.loadImage("levels/" + level + "_tiles.png");
            BufferedImage tileImage = null;
            tiles = new Tile[backgroundImage.getWidth()][backgroundImage.getHeight()];

            for (int i = 0; i < tiles.length; i++) {
                for (int j = 0; j < tiles[i].length; j++) {
                    tiles[i][tiles[i].length - j - 1] = new Tile(new Color(backgroundImage.getRGB(i, j)), i, tiles[i].length - j - 1);

                    if (j != 0 && tiles[i][tiles[i].length - j - 1].r == 0 && (tiles[i][tiles[i].length - j].r == 0  || tiles[i][tiles[i].length - j].r == 2)) {
                        tiles[i][tiles[i].length - j - 1].r = 2;
                    }
                    
                    if (!usedTileIds.contains(tiles[i][tiles[i].length - j - 1].r)) {
                        tileImage = display.loadImage("tiles/" + tiles[i][tiles[i].length - j - 1].r + ".png");
                        usedTileImages.add(tileImage);
                        usedTileIds.add(tiles[i][tiles[i].length - j - 1].r);
                    }
                }
            }
        } catch (NullPointerException e) {
            display.handleException(e, "Tile image not found");
        } catch (Exception e) {
            display.handleException(e, "Unknown cause");
        }
        
        display.log("Tiles image read");
    }

    /**
     * Determines the x offset for the level
     */
    public void setXOffset() {
        int smallDifference = tileSize * 7;
        int bigDifference = tileSize * 9;

        if (player.x - smallDifference < xOffset) {
            xOffset = player.x - smallDifference;

            if (xOffset < 0) {
                xOffset = 0;
            }
        }

        if (player.x + smallDifference - (xOffset + bigDifference) > smallDifference) {
            xOffset = player.x - bigDifference;

            if (xOffset < 0) {
                xOffset = 0;
            }

            if (xOffset + bigDifference > tiles.length * tileSize - smallDifference) {
                xOffset = tiles.length * tileSize - 16 * tileSize; // 16 is amount of tiles displayed on screen (width)
            }
        }
    }

    /**
     * Determines the y offset for the level
     */
    public void setYOffset() {
        int smallDifference = tileSize * 4;
        int bigDifference = tileSize * 5;
        
        //System.out.println(player.x + "\t" + (player.x - smallDifference) + "\t" + yOffset);
        
        if (player.y - smallDifference < yOffset) {
            yOffset = player.y - smallDifference;
            
            if (yOffset < 0) {
                yOffset = 0;
            }
        }

        if (player.y + smallDifference - (yOffset + bigDifference) > smallDifference) {
            yOffset = player.y - bigDifference;
            
            if (yOffset < 0) {
                yOffset = 0;
            }
            
            if (yOffset + bigDifference - tileSize > tiles[0].length * tileSize - smallDifference) {
                yOffset = tiles[0].length * tileSize - 9 * tileSize; // 9 is amount of tiles displayed on screen (height)
            }
        }
    }

}
