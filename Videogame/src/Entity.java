import java.awt.*;
import java.awt.geom.AffineTransform;
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
    protected boolean facingRight = true;  // Dirección a la que mira la entidad

    /**
     * Constructor básico que inicializa la entidad con posición y dimensiones.
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param width Ancho de la entidad
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
     * @param path Ruta del archivo de imagen
     */
    public void loadImage(String path) {
        try {
            sprite = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la lógica de la entidad (movimiento, física, etc).
     * Este métod0 debe ser sobrescrito por las clases hijas.
     */
    public void update() {
        // Aplica la gravedad
        velocityY += gravity;
        y += (int) velocityY;
    }

    // Dibuja la entidad en el panel de juego.
//    public void render(Graphics2D g2) {
//        if (sprite != null) {
//            AffineTransform transform = new AffineTransform();
//
//            // Aplica la transformación según la dirección
//            if (!facingRight) {
//                transform.translate(x + width, y);  // Posición para imagen volteada
//                transform.scale(-1, 1);  // Voltea horizontalmente
//            } else {
//                transform.translate(x, y);  // Posición normal
//            }
//
//            // Escala la imagen al tamaño de la entidad
//            transform.scale((double) width / sprite.getWidth(null),
//                    (double) height / sprite.getHeight(null));
//
//            // Dibuja la imagen con las transformaciones aplicadas
//            g2.drawImage(sprite, transform, null);
//        }
//    }
}