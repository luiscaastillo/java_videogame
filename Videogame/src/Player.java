/**
 * Clase que representa al jugador en el juego.
 * Extiende la clase Entity y añade funcionalidades específicas del jugador.
 */
public class Player extends Entity {
    // Referencia al controlador de teclado
    private final KeyHandler keyH;

    /**
     * Constructor del jugador.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param width Ancho del jugador
     * @param height Alto del jugador
     * @param speed Velocidad de movimiento
     * @param keyH Controlador de teclado
     */
    public Player(int x, int y, int width, int height, int speed, KeyHandler keyH) {
        super(x, y, width, height);
        this.speed = speed;
        this.keyH = keyH;
    }

    /**
     * Actualiza la lógica del jugador (movimiento, salto, etc).
     * @param screenWidth Ancho de la pantalla
     * @param screenHeight Alto de la pantalla
     */
    public void update(int screenWidth, int screenHeight) {
        int floor = screenHeight - height;  // Posición Y del suelo

        // Salto cuando se presiona la tecla arriba y el jugador está en el suelo
        if (keyH.upPressed && y + height >= floor) {
            // Constantes específicas del jugador
            velocityY = -15;  // Fuerza de salto negativa (hacia arriba)
        }
        if (keyH.downPressed) {
            // Constantes específicas del jugador
            velocityY = 10;  // Caer más rápido
        }
        // Aplica la gravedad (llamada al métod0 de la clase padre)
        super.update();

        // Detecta colisión con el suelo
        if (y + height > floor) {
            y = floor - height;  // Coloca al jugador en el suelo
            velocityY = 0;       // Detiene la caída
        }
/*
        // Movimiento horizontal
        if (keyH.leftPressed) {
            x -= speed;
            if (x < 0) {
                x = 0;  // Evita que el jugador salga por la izquierda
            }
            facingRight = false;  // Actualiza la dirección
        }
        if (keyH.rightPressed) {
            x += speed;
            if (x + width > screenWidth) {
                x = screenWidth - width;  // Evita que el jugador salga por la derecha
            }
            facingRight = true;  // Actualiza la dirección
        }
*/
    }
}