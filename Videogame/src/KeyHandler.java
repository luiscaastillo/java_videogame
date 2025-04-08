import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Clase que maneja los eventos de teclado para controlar al jugador.
 * Implementa KeyListener para detectar cuando se presionan y sueltan teclas.
 */
public class KeyHandler implements KeyListener {

    // Variables que indican si las teclas de dirección están presionadas
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    /**
     * Método invocado cuando se tipea una tecla (no usado en este juego).
     */
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
        // No se implementa funcionalidad para este método
    }

    /**
     * Método invocado cuando se presiona una tecla.
     * Actualiza las variables de dirección según la tecla presionada.
     */
    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        int code = e.getKeyCode();  // Obtiene el código de la tecla presionada

        // Actualiza las variables de estado según la tecla presionada
        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
    }

    /**
     * Método invocado cuando se suelta una tecla.
     * Actualiza las variables de dirección cuando se deja de presionar una tecla.
     */
    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        int code = e.getKeyCode();  // Obtiene el código de la tecla liberada

        // Actualiza las variables de estado cuando se suelta una tecla
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }
}