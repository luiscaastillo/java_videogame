import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Entity {
    // Posición y dimensiones
    public int x, y;                 // Coordenadas de posición en el mapa
    public int width, height;        // Dimensiones de la entidad
    public int speed;                // Velocidad de movimiento

    // Física
    protected double velocityY = 0;  // Velocidad vertical
    protected final double gravity = 0.5;  // Constante de gravedad

    // Imagen y dirección
    protected Image sprite;          // Imagen de la entidad

    // Constructor
    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = 2 * width;
        this.height = 2 * height;
    }

    // Métod para dibujar la entidad en el panel de juego
    public void loadImage(String path) {
        try {
            sprite = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Métod para dibujar la entidad en el panel de juego
    public void update() {
        // Aplica la gravedad
        velocityY += gravity;
        y += (int) velocityY;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}