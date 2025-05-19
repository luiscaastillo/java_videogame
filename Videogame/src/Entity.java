import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Clase base para todas las entidades del juego.
 * Contiene las propiedades básicas que cualquier entidad necesita.
 */
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

    /**
     * Constructor básico que inicializa la entidad con posición y dimensiones.
     *
     * @param x      Posición X inicial
     * @param y      Posición Y inicial
     * @param width  Ancho de la entidad
     * @param height Alto de la entidad
     */
    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = 2 * width;
        this.height = 2 * height;
    }

    /**
     * Carga la imagen de la entidad desde un archivo.
     *
     * @param path Ruta del archivo de imagen
     */
    public void loadImage(String path) {
        try {
            sprite = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // Aplica la gravedad
        velocityY += gravity;
        y += (int) velocityY;
    }
}