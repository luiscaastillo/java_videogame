import java.awt.Color;
import java.awt.Graphics2D;

public class Platform extends Entity {
    public int x, y, width, height;

    public Platform(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Draws the platform
    public void render(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, width, height);
    }
}