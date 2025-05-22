// Projectile.java
import java.awt.*;

public class Projectile {
    public int x, y, width, height, speed, direction;
    public boolean active = true;

    public Projectile(int x, int y, int width, int height, int speed, int direction) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.direction = direction; // -1 for left, 1 for right
    }

    public void update() {
        x += speed * direction;
        // Optionally deactivate if off screen
        if (x < -width || x > 1920 + width) active = false;
    }

    public void render(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillRect(x, y, width, height);
    }
}