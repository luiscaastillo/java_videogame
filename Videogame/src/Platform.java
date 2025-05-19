import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Platform extends Entity {
    public int x, y, width, height;

    public Platform(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Returns the bounding box for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Draws the platform
    public void render(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, width, height);
    }
}