package caverunner;

import java.awt.Dialog;
import java.awt.Image;
import java.util.Arrays;
import java.util.List;
import javax.swing.JDialog;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * Main class
 * @author Lord Byron's Army
 */
public class Main {

    static JDialog win;
    static Display display;
    static Input input;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        win = new JDialog((Dialog) null);
        display = new Display("title");
        display.log("Program started");
        
        Resolution r = new Resolution(display);
        r.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        r.setResizable(false);
        r.setLocationRelativeTo(null);
        r.setVisible(true);
        
        while (!display.ready) {
            System.out.print("");
        }
        
        r.dispose();
        
        if (display.width == -1 || display.height == -1) {
            display.log("Game terminated before start");
            System.exit(0);
        }
        
        display.log("Launch of game");
        
        //display.game = new Game(display, 2);
        //display.game.update = true;
        display.playMusic("game_music", true);
        display.title = new Title(display);
        input = new Input(display);
        win.setResizable(false);
        win.setSize(display.width, display.height);
        win.setLocationRelativeTo(null);
        win.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        win.setUndecorated(true);
        win.add(display);
        win.setVisible(true);
        win.addMouseListener(input);
        win.addKeyListener(input);
        
        List<Image> icons = Arrays.asList(
                display.loadImage("icon_16.png"),
                display.loadImage("icon_32.png"),
                display.loadImage("icon_64.png"));
        win.setIconImages(icons);
    }
    
}
