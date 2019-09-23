package caverunner;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Input that collects key actions and mouse actions
 * @author Lord Byron's Army
 */
class Input extends KeyAdapter implements MouseListener {

    Display display;
    boolean escape = false;
    boolean tilde = false;
    final int LEFT = KeyEvent.VK_LEFT;
    final int RIGHT = KeyEvent.VK_RIGHT;
    final int JUMP = KeyEvent.VK_SPACE;
    final int ATTACK = KeyEvent.VK_Z;
    
    /**
     * Constructor
     * @param display 
     */
    public Input(Display display) {
        this.display = display;
        display.log("Input created");
    }
    
    /**
     * Runs whenever a key is pressed
     * @param e 
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                escape = true;
                break;
            case KeyEvent.VK_BACK_QUOTE:
                tilde = true;
                break;
        }
        
        if (display.currentClass.equals("game")) {
            switch (e.getKeyCode()) {
                case LEFT:
                    display.game.player.direction = "left";
                    display.game.player.leftButton = true;
                    break;
                case RIGHT:
                    display.game.player.direction = "right";
                    display.game.player.rightButton = true;
                    break;
                case JUMP:
                    display.game.player.justJumped = !display.game.player.jumpButton;
                    display.game.player.jumpButton = true;
                    
                    if (display.game.player.justJumped) {
                        display.game.player.jumping = true;
                    }
                    
                    break;
                case KeyEvent.VK_ENTER:
                    if (display.game.tutorial) {
                        display.game.tutorial = false;
                    }
                    
                case ATTACK:
                    display.game.player.attack("lanturn");
                    break;
            }
        }
        
        if (escape && tilde) {
            display.log("Program terminated");
            System.exit(0);
        }
    }
    
    /**
     * Runs whenever a key is released
     * @param e 
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                escape = false;
                break;
            case KeyEvent.VK_BACK_QUOTE:
                tilde = false;
                break;
        }
        
        if (display.game != null) {
            switch (e.getKeyCode()) {
                case LEFT:
                    display.game.player.leftButton = false;
                    
                    if (display.game.player.rightButton) {
                        display.game.player.direction = "right";
                    } else {
                        display.game.player.xMomentum = 0;
                    }
                    
                    break;
                case RIGHT:
                    display.game.player.rightButton = false;
                    
                    if (display.game.player.leftButton) {
                        display.game.player.direction = "left";
                    } else {
                        display.game.player.xMomentum = 0;
                    }
                    
                    break;
                case JUMP:
                    display.game.player.jumping = false;
                    display.game.player.jumpButton = false;
                    display.game.player.falling = true;
            }
        }
    }
    
    /**
     * Required for MouseListener
     * @param evt 
     */
    @Override
    public void mouseClicked(MouseEvent evt) {
        
    }

    /**
     * Listens to when the mouse is pressed
     * @param evt 
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        if (display.currentClass.equals("title") && display.title.startButtonHitBox.getBounds().contains(evt.getPoint())) {
            display.game = new Game(display, 1);
            display.currentClass = "game";
        }
    }

    /**
     * Required for MouseListener
     * @param evt 
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        
    }

    /**
     * Required for MouseListener
     * @param evt 
     */
    @Override
    public void mouseEntered(MouseEvent evt) {
        
    }

    /**
     * Required for MouseListener
     * @param evt 
     */
    @Override
    public void mouseExited(MouseEvent evt) {
        
    }
    
    
    
}
