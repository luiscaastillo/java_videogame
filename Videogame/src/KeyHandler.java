import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Handles keyboard input for the game, tracking movement and escape keys, and managing game state transitions.
public class KeyHandler implements KeyListener {
    // True if the 'W' key is pressed.
    public boolean upPressed;
    // True if the 'S' key is pressed.
    public boolean downPressed;
    // True if the 'A' key is pressed.
    public boolean leftPressed;
    // True if the 'D' key is pressed.
    public boolean rightPressed;
    // True if the 'Escape' key is pressed.
    public boolean escapePressed;

    // Reference to the game panel for state management.
    private final GamePanel gamePanel;

    // Constructs a KeyHandler with the specified GamePanel.
    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    // Not used, but required by the KeyListener interface.
    @Override
    public void keyTyped(KeyEvent e) {}

    // Handles key press events.
    // Sets movement and escape flags, and manages state transitions on ENTER.
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if ((gamePanel.getGameState() == GameState.MENU || gamePanel.getGameState() == GameState.GAME_OVER)
                && code == KeyEvent.VK_ENTER) {
            gamePanel.setGameState(GameState.PLAYING_LEVEL3);
        }

        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
        if (code == KeyEvent.VK_ESCAPE) escapePressed = true;
    }

    // Handles key release events.
    // Resets movement and escape flags.
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        if (code == KeyEvent.VK_ESCAPE) escapePressed = false;
    }
}
