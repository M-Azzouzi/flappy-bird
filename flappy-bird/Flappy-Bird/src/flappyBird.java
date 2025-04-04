import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.io.*;
import javax.swing.JOptionPane;

public class flappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;
    boolean gameStart = false;
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdX = boardWidth/8 ;
    int birdY = boardHeight/2 ;
    int birdWidth = 54;
    int birdHeight = 44;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    Bird bird;
    int velocityX = -5   ;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;


    javax.swing.Timer gameLoop;
    javax.swing.Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    int highScore = 0;
    String highScoreFile = "highscore.txt";

    flappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("./imgs/background.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./imgs/bird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./imgs/pipetop.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./imgs/pipebottom.png")).getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<>();
        loadHighScore();

        placePipeTimer = new javax.swing.Timer(2000,e -> placePipes());


        gameLoop = new javax.swing.Timer(1000/50, this);

    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace =  boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("DISCO RETRO", Font.PLAIN, 32));
        g.drawString("Score: " + (int) score, 10, 35);
        g.drawString("High Score: " + highScore, 10, 70);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Game Over", posx - 30, posy);
        }

        if (!gameStart) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Press SPACE to Start", boardWidth / 18, boardHeight / 2);
        }
    }

    int posx , posy;

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < pipes.size(); i += 2) {
            Pipe topPipe = pipes.get(i);
            Pipe bottomPipe = pipes.get(i + 1);

            topPipe.x += velocityX;
            bottomPipe.x += velocityX;

            if (!topPipe.passed && bird.x > topPipe.x + topPipe.width) {
                score += 1;
                topPipe.passed = true;
            }

            if (collision(bird, topPipe) || collision(bird, bottomPipe)) {
                gameOver = true;


                posx = topPipe.x + topPipe.width / 2 - 50;
                posy = topPipe.y + pipeHeight + (boardHeight / 6) / 2;
                System.out.println(  posx + " " + posy);
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }


    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    void loadHighScore() {
        try (Scanner scanner = new Scanner(new File(highScoreFile))) {
            if (scanner.hasNextInt()) {
                highScore = scanner.nextInt();
            }
        } catch (IOException e) {
            System.out.println("No previous high score found.");
        }
    }

    void saveHighScore() {
        if (score > highScore) {
            highScore = (int) score;
            String name = JOptionPane.showInputDialog("New High Score! Enter your name:");
            try (PrintWriter writer = new PrintWriter(new FileWriter(highScoreFile))) {
                writer.println(highScore);
                writer.println(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStart) {
            move();
            repaint();
            placePipeTimer.start();
            if (gameOver) {
                placePipeTimer.stop();
                gameLoop.stop();
                saveHighScore();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStart && !gameOver) {

                gameStart = true;
                velocityY=-9;
                gameLoop.start();
                placePipeTimer.start();
            } else if (gameOver) {

                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameStart = false;
                repaint();
            } else {

                velocityY = -9;
            }
        }
    }



    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}