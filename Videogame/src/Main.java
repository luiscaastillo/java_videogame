import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // Create the main game window with a specific title.
        JFrame window = new JFrame("DE REGRESO DEL MICTLÁN");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setSize(1280, 960);

        // Create and add the game panel to the window.
        GamePanel gamePanel = new GamePanel();

        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Start the main game loop.
        gamePanel.startGameThread();
    }
}
