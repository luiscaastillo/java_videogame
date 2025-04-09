import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Clase que representa al jugador en el juego.
 * Extiende la clase Entity y añade funcionalidades específicas del jugador.
 */
public class Player extends Entity {
    private final KeyHandler keyH;
    private final Image[] runningImages = new Image[2]; // Imágenes para la animación
    private int animationIndex = 0; // Índice de la imagen actual
    private int animationCounter = 0; // Contador para controlar la velocidad de animación

    public Player(int x, int y, int width, int height, int speed, KeyHandler keyH) {
        super(x, y, width, height);
        this.speed = speed;
        this.keyH = keyH;

        // Carga las imágenes de animación
        try {
            runningImages[0] = ImageIO.read(new File("Videogame/src/assets/player1.png"));
            runningImages[1] = ImageIO.read(new File("Videogame/src/assets/player2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(int screenHeight) {
        int floor = screenHeight - height;

        // Salto
        if (keyH.upPressed && y + height >= floor) {
            velocityY = -15;
        }

        if (keyH.downPressed) {
            velocityY = 10;
        }

        // Gravedad
        super.update();

        // Colisión con el suelo
        if (y + height > floor) {
            y = floor - height;
            velocityY = 0;
        }

        // Actualiza la animación
        animationCounter++;
        // Velocidad de cambio de imagen
        int animationSpeed = 10;
        if (animationCounter >= animationSpeed) {
            animationCounter = 0;
            animationIndex = (animationIndex + 1) % runningImages.length; // Alterna entre 0 y 1
        }
    }

    public void render(Graphics2D g2) {
        // Dibuja la imagen actual de la animación
        if (runningImages[animationIndex] != null) {
            g2.drawImage(runningImages[animationIndex], x, y, width, height, null);
        }
    }
}