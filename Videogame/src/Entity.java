import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Entity {
    // Position and size
    public int x, y;                 // Position coordinates on the map
    public int width, height;        // Entity dimensions
    public int speed;                // Movement speed

    // Physics
    protected double velocityY = 0;  // Vertical velocity
    protected final double gravity = 0.5;  // Gravity constant

    // Image and direction
    protected Image sprite;          // Entity image

    // Constructor to initialize position and size
    public Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = 2 * width;
        this.height = 2 * height;
    }

    // Loads the entity's image from the given path.
    public void loadImage(String path) {
        try {
            sprite = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Updates the entity's position and applies gravity.
    public void update() {
        velocityY += gravity;
        y += (int) velocityY;
    }

    // Returns the bounding rectangle for collision detection.
    public Rectangle getBounds() {
        int insetX = width / 6;
        int insetY = height / 6;
        int w = width - 2 * insetX;
        int h = height - 2 * insetY;
        return new Rectangle(x + insetX, y + insetY, w, h);
    }
}
