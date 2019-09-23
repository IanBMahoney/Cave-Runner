package caverunner;

import java.awt.Color;

/**
 * Tile class holds information for every tile in the game in a 2D array
 * @author Lord Byron's Army
 */
public class Tile {
    
    int x, xEnd, y, yEnd;
    int r, g, b;
    int id;
    
    /**
     * Constructor
     * @param color
     * @param x
     * @param y 
     */
    public Tile(Color color, int x, int y) {
        this.x = x * 16;
        this.xEnd = this.x + 15;
        this.y = y * 16;
        this.yEnd = this.y + 15;
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.id = r;
    }
    
}
