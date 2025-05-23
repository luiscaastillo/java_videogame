import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

// Platform class represents a platform entity in the game.
public class Platform extends Entity {
    // Shared image for all platform instances.
    private static Image platformImage;
    // Type identifier for the platform.
    protected final int type;

    // Static block to load the platform image resource.
    static {
        try {
            platformImage = ImageIO.read(new File("Videogame/src/assets/platform.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Constructs a Platform with specified position, size, and type.
    public Platform(int x, int y, int width, int height, int type) {
        super(x, y, width, height);
        this.type = type;
    }

    // Renders the platform using the loaded image.
    // If the image is not available, draws a rectangle as a fallback.
    public void render(Graphics2D g2) {
        if (platformImage != null) {
            int stretchWidth = 150;
            int stretchHeight = 40;
            g2.drawImage(
                    platformImage,
                    x - stretchWidth / 2,
                    y - stretchHeight,
                    width + stretchWidth,
                    height + stretchHeight,
                    null
            );
        } else g2.fillRect(x, y, width, height);
    }

    // Returns the collision bounds of the platform.
    public Rectangle getBounds() {
        return new Rectangle(x, y, width - 20, height - 50);
    }
}
