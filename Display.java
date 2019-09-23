package caverunner;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * Class that maintains all objects and paints to the screen
 *
 * @author Lord Byron's Army
 */
public class Display extends JPanel {

    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss:SSS");
    
    ArrayList<String> highScores;
    Timer timer;
    Game game;
    Title title;
    String currentClass;
    Clip music;
    int width = -1;
    int height = -1;
    double scale = -1;
    boolean pause = false;
    boolean ready = false;

    /**
     * Constructor
     *
     * @param startingClass
     */
    public Display(String startingClass) {
        currentClass = startingClass;
        highScores = readFile("high_scores.txt");
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int num = 0;

            @Override
            public void run() {
                if (pause) {
                    return;
                }

                if (game != null && game.update && currentClass.equals("game")) {
                    num++;

                    game.player.updateX();
                    game.player.updateY();
                    game.player.updateHealth();
                    
                    if (game.tiles[game.player.x / 16][game.player.y / 16].r == 100) {
                        game.gameOver();
                    }
                    
                    for (int i = 0; i < game.entities.size(); i++) {
                        if (game.entities.get(i).onScreen()) {
                            game.entities.get(i).enabled = true;
                        }

                        if (game.entities.get(i).enabled) {
                            game.entities.get(i).updateX();
                            game.entities.get(i).updateY();
                        }

                        if (game.entities.get(i).disappear) {
                            game.entities.remove(i);
                            i--;
                        }
                    }

                    game.setXOffset();
                    game.setYOffset();
                    game.player.checkForCollisions();
                } else if (title != null && currentClass.equals("title")) {

                }

                repaint();
            }
        }, 0, 1000 / 60);
    }

    /**
     * Loads images associated with the entity
     */
    
    
    /**
     * Reverses the order of high scores and removes scores worse than top 5
     * @param array
     * @return list of high scores
     */
    public ArrayList<String> reverseScores(ArrayList<String> array) {
        ArrayList<String> newArray = new ArrayList();

        for (int i = 0; i < array.size(); i++) {
            newArray.add(array.get(array.size() - 1 - i));
        }

        while (newArray.size() > 5) {
            newArray.remove(newArray.size() - 1);
        }

        return newArray;
    }
    
    /**
     * Plays a WAV file
     * @param fileName
     * @param isMusic 
     */
    public void playMusic(String fileName, boolean isMusic) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("sounds/" + fileName + ".wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            if (isMusic) {
                try {
                    music.stop();
                } catch (Exception e) {

                }

                music = clip;
                music.loop(Clip.LOOP_CONTINUOUSLY);
                music.start();
            } else {
                clip.start();
            }
        } catch (Exception e) {
            handleException(e, "Problem playing sound \"" + fileName + "\"");
        }
    }

    /**
     * Reads a file
     * @param fileName
     * @return ArrayList of strings
     */
    public ArrayList<String> readFile(String fileName) {
        try {
            ArrayList<String> lines = new ArrayList();
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }

            return lines;
        } catch (Exception e) {
            handleException(e, "Problem with reading file");
        }

        return null;
    }

    /**
     * Writes a string to a file
     * @param fileName
     * @param information
     * @param override 
     */
    public void writeToFile(String fileName, String information, boolean override) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, !override);
            fileWriter.write(information);

            if (!information.endsWith("\n")) {
                fileWriter.write("\n");
            }

            fileWriter.close();
        } catch (IOException e) {
            handleException(e, "Problem writing to file");
        } catch (Exception e) {
            handleException(e, "Unknown error");
        }
    }

    /**
     * Writes an ArrayList of strings to a file
     * @param fileName
     * @param information
     * @param override 
     */
    public void writeToFile(String fileName, ArrayList<String> information, boolean override) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, !override);

            for (int i = 0; i < information.size(); i++) {
                fileWriter.write(information.get(i));

                if (!information.get(i).endsWith("\n")) {
                    fileWriter.write("\n");
                }
            }

            fileWriter.close();
        } catch (IOException e) {
            handleException(e, "Problem writing to file");
        } catch (Exception e) {
            handleException(e, "Unknown error");
        }
    }

    /**
     * Logs information to a txt file
     * @param message 
     */
    public void log(String message) {
        writeToFile("log.txt", sdf.format(Calendar.getInstance().getTime()) + ": " + message, false);
    }

    /**
     * Sends a popup to the user and reports to a txt
     * @param e
     * @param potentialReason 
     */
    public void handleException(Exception e, String potentialReason) {
        String time = "" + sdf.format(Calendar.getInstance().getTime());

        ArrayList<String> messages = new ArrayList();
        messages.add(time + ": Potential problem: " + potentialReason);
        messages.add(time + ": " + e.getMessage());

        for (int i = 0; i < e.getStackTrace().length; i++) {
            messages.add(time + ": " + e.getStackTrace()[i].toString());
        }
        e.printStackTrace();
        writeToFile("exceptions.txt", messages, false);

        if (!pause) {
            pause = true;

            Error err = new Error(this, "<html>Potential problem: " + potentialReason + "<br>" + e.getMessage() + "</html>");
            err.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            err.setResizable(false);
            err.setLocationRelativeTo(null);
            err.setVisible(true);
        }
    }

    /**
     * Loads an image from the game file
     *
     * @param fileName
     * @return image
     */
    public BufferedImage loadImage(String fileName) {
        BufferedImage bi = null;
        //System.out.println(getClass().toString());
        try {
            bi = ImageIO.read(getClass().getResourceAsStream(fileName));
        } catch (Exception e) {
            handleException(e, "Loading image \"" + fileName + "\" failed");
            //e.printStackTrace();
        }

        return bi;
    }

    /**
     * Sets screen width
     *
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
        scale = (double) width / (double) 1280 * 5;
        System.out.println(scale);
    }

    /**
     * Sets screen height
     *
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets game
     *
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Decides what needs to be painted
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        
        try {
            System.out.println("paint");
            if (currentClass.equals("game") && game == null) {
            } else if (currentClass.equals("game")) {
                System.out.println("game ran");
                paintGameBackground(g);
                paintPlayer(g);
                paintEntities(g);
                paintShadow(g);
                paintPlayerHealth(g);

                if (game.tutorial) {
                    paintTutorial(g);
                }
            } else if (currentClass.equals("title")) {
                System.out.println("title ran");
                paintTitle(g);
            } else if (currentClass.equals("options")) {

            } else {
                System.out.println("System has no current class! Ending program");
                System.exit(0);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            handleException(e, "Missing image");
        } catch (Exception e) {
            handleException(e, "Unknown cause");
        }
    }

    public void paintLoading(Graphics g) {
        g.drawImage(title.loadingScreen, 0, 0, width, height, this);
    }

    /**
     * Paints the title
     *
     * @param g
     */
    public void paintTitle(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

//        g.drawString("kappa", 50, 50);
//                System.out.println("About to be printed");
        g2.draw(title.startButtonHitBox);
        g.drawImage(title.titleScreen, 0, 0, width, height, this);
        g.drawImage(title.startButton, width / 2 - width / 8, height / 2, width / 4, height / 7, this);
//        System.out.println("It should have printed");

        g.setColor(Color.gray);
        g.setFont(new Font("Chalkduster", 1, 30));
        for (int i = 0; i <= 4; i++) {
            g.drawString((i + 1) + ": " + highScores.get(i), width - width / 5, height / 2 + height / 18 + (i * (height / 20)));//high scores that create weird image top-right
        }
    }

    /**
     * Paints the player health
     *
     * @param g
     */
    public void paintPlayerHealth(Graphics g) {
        int num = (int) ((double) game.tileSize * scale);

        g.drawImage(game.player.healthImage, num / 2 + num * 6, num / 2, num * 3, num, this);
        g.drawImage(game.player.healthbarImages.get((int) ((double) game.player.health / 100.0 * 36.0)), num / 2 + num * 6, num / 2, num * 3, num, this);
//g.setColor(new Color(200, 200, 200));
        //g.fillRect(num / 2, num / 2, num * 3, num);
    }

    public void paintTutorial(Graphics g) {
        int num = (int) ((double) game.tileSize * scale);
        g.drawImage(game.player.tutorialImage, num * 13 - num / 2, num / 2, num * 3, num * 5, this);
    }

    /**
     * Paints the shadow
     *
     * @param g
     */
    public void paintShadow(Graphics g) {
        int num = (int) ((double) game.tileSize * scale);

        g.drawImage(
                game.player.getShadowImage(),
                game.player.x * num / game.tileSize - game.xOffset * num / game.tileSize - width + num / 2,
                num * (144 - game.player.y) / game.tileSize - game.player.height * num / game.tileSize + game.yOffset * num / game.tileSize - height + num, //  + game.player.y * num / 16    add this back after fix
                width * 2,
                height * 2,
                this);
    }

    /**
     * Paints the player
     *
     * @param g
     */
    public void paintPlayer(Graphics g) {
        int num = (int) ((double) game.tileSize * scale);
        g.drawImage(
                game.player.getImage(),
                game.player.x * num / game.tileSize - game.xOffset * num / game.tileSize,
                num * (144 - game.player.y) / game.tileSize - game.player.height * num / game.tileSize + game.yOffset * num / game.tileSize, //  + game.player.y * num / 16    add this back after fix
                game.player.width * num / game.tileSize,
                game.player.height * num / game.tileSize,
                this);
    }

    /**
     * Paints the entities
     *
     * @param g
     */
    public void paintEntities(Graphics g) { // when entities have a negative y value, remove them from array because theyre dead
        int num = (int) ((double) game.tileSize * scale);

        for (int i = 0; i < game.entities.size(); i++) { // add something to only display entities that are on screen
            g.drawImage(
                    game.entities.get(i).getImage(),
                    game.entities.get(i).x * num / game.tileSize - game.xOffset * num / game.tileSize,
                    num * (144 - game.entities.get(i).y) / game.tileSize - game.entities.get(i).height * num / game.tileSize + game.yOffset * num / game.tileSize, //  + game.player.y * num / 16    add this back after fix
                    game.entities.get(i).width * num / game.tileSize,
                    game.entities.get(i).height * num / game.tileSize,
                    this);
        }
    }

    /**
     * Paints the game background
     *
     * @param g
     */
    public void paintGameBackground(Graphics g) {
        Tile[][] tiles = game.tiles;
        int num = (int) (game.tileSize * scale);

        for (int i = game.xOffset / 16; i <= game.xOffset / 16 + 16 && i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (game.tiles[i][tiles[i].length - j - 1].g == 100) {
                    g.drawImage(
                            game.usedTileImages.get(0),
                            tiles[i][tiles[0].length - j - 1].x / game.tileSize * num - game.xOffset * num / game.tileSize,
                            height - num - tiles[i][tiles[i].length - j - 1].y / game.tileSize * num + game.yOffset * num / game.tileSize,
                            num,
                            num,
                            this);
                }

                g.drawImage(
                        game.getImageFromTile(tiles[i][tiles[0].length - j - 1]),
                        tiles[i][tiles[0].length - j - 1].x / game.tileSize * num - game.xOffset * num / game.tileSize,
                        height - num - tiles[i][tiles[i].length - j - 1].y / game.tileSize * num + game.yOffset * num / game.tileSize,
                        num,
                        num,
                        this);

                //g.setColor(Color.blue);
                //g.drawString("X: " + tiles[i][j].x + "-" + tiles[i][j].xEnd, tiles[i][j].x / 16 * num, 10 + height - num - tiles[i][j].y / 16 * num);
                //g.drawString("Y: " + tiles[i][j].y + "-" + tiles[i][j].yEnd, tiles[i][j].x / 16 * num, 25 + height - num - tiles[i][j].y / 16 * num);
            }
        }
    }

}
