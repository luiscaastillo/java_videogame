import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyHandler implements KeyListener {

    // Variables que indican si las teclas de dirección están presionadas
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    private final GamePanel gamePanel;
    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    // Métod0 invocado cuando se presiona una tecla.
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
        // No se implementa funcionalidad para este Métod0
    }

    // Métod invocado cuando sepresiona una tecla.
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Cambia el estado del juego según la tecla presionada
        if (gamePanel.getGameState() == GameState.MENU && code == KeyEvent.VK_ENTER) {
            gamePanel.setGameState(GameState.PLAYING);
        } else if (gamePanel.getGameState() == GameState.PLAYING && code == KeyEvent.VK_ESCAPE) {
            gamePanel.setGameState(GameState.PAUSED);
        } else if (gamePanel.getGameState() == GameState.PAUSED && code == KeyEvent.VK_ESCAPE) {
            gamePanel.setGameState(GameState.PLAYING);
        }

        // Actualiza las variables de estado según la tecla presionada
        if (code == KeyEvent.VK_W)
            upPressed = true;
        if (code == KeyEvent.VK_A)
            leftPressed = true;
        if (code == KeyEvent.VK_S)
            downPressed = true;
        if (code == KeyEvent.VK_D)
            rightPressed = true;
    }

    // Métod invocado cuando se suelta una tecla.
    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        int code = e.getKeyCode();  // Obtiene el código de la tecla liberada

        // Actualiza las variables de estado cuando se suelta una tecla
        if (code == KeyEvent.VK_W)
            upPressed = false;
        if (code == KeyEvent.VK_A)
            leftPressed = false;
        if (code == KeyEvent.VK_S)
            downPressed = false;
        if (code == KeyEvent.VK_D)
            rightPressed = false;
    }
}