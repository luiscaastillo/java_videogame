import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.util.List;

public class Player extends Entity {
    private final KeyHandler keyH;
    private final Image[] runningImages = new Image[2]; // Imágenes para la animación
    private int animationIndex = 0; // Índice de la imagen actual
    private int animationCounter = 0; // Contador para controlar la velocidad de animación
    private final int originalX;
    private final int originalY;

    public Player(int x, int y, int width, int height, int speed, KeyHandler keyH) {
        super(x, y, width, height);
        this.speed = speed;
        this.keyH = keyH;
        this.originalX = x;
        this.originalY = y;

        // Carga las imágenes de animación
        try {
            runningImages[0] = ImageIO.read(new File("Videogame/src/assets/player1.png"));
            runningImages[1] = ImageIO.read(new File("Videogame/src/assets/player2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(int screenHeight, List<Platform> platforms) {

        // Platform collision
        boolean onPlatform = false;
        Rectangle playerBounds = new Rectangle(x, (int) (y + velocityY), width, height);
        for (Platform p : platforms) {
            Rectangle platBounds = p.getBounds();
            // Check if player is falling and lands on top of the platform
            if (velocityY >= 0 && playerBounds.intersects(platBounds) && y + height <= p.y + velocityY) {
                y = p.y - height;
                velocityY = 0;
                onPlatform = true;
                break;
            }
        }

        // Jump
        if (keyH.upPressed && (y + height >= screenHeight ||onPlatform)) {
            velocityY = -15;
        }

        if (keyH.downPressed) {
            velocityY = 10;
        }

        // Gravity
        super.update();


        // Floor collision if not on any platform
        if (!onPlatform && y + height> screenHeight) {
            y = screenHeight - height;
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

    public void resetPosition() {
        this.x = originalX;
        this.y = originalY;
    }
}