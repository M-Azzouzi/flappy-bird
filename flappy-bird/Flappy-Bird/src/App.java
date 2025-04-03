import javax.swing.*;
public class App {
    public static void main(String[] args) {
       int boardWidth = 800;
         int boardHeight = 600;

        JFrame frame = new JFrame("Flappy Bird Knockoff");
        frame.setTitle("Flappy Bird Knockoff");
        
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);

        flappyBird game = new flappyBird();
        frame.add(game);
        frame.pack();

        frame.setVisible(true);
    }
}
