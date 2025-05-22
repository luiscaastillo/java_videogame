import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends Entity {
    private List<Projectile> projectiles = new ArrayList<>();
    private Player player;
    private Image sprite1;
    private Image sprite2;
    private boolean useFirstSprite = true;
    private int animationCounter = 0;

    private int shootCounter = 0;

    public Enemy(int x, int y, int width, int height, String spritePath1, String spritePath2) {
        super(x, y, width, height);
//        try {
//            sprite1 = ImageIO.read(new File(spritePath1));
//            sprite2 = ImageIO.read(new File(spritePath2));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        sprite = sprite1;
    }

    @Override
    public void update() {
        // Alternate sprite for animation
        animationCounter++;
        // frames
        int animationInterval = 30;
        if (animationCounter >= animationInterval) {
            useFirstSprite = !useFirstSprite;
            sprite = useFirstSprite ? sprite1 : sprite2;
            animationCounter = 0;
        }

        // Shooting logic
        shootCounter++;
        // frames
        int shootInterval = 90;
        if (shootCounter >= shootInterval) {
            shoot();
            shootCounter = 0;
        }

        for (Projectile p : projectiles) {
            p.update();
        }
        projectiles.removeIf(p -> !p.active);
    }

    public void shoot() {
        if (player == null) return;
        int direction = (player.x + player.width / 2) < (x + width / 2) ? -1 : 1;
        int projX = direction == -1 ? x : x + width;
        int projY = y + height / 2 - 5;
        projectiles.add(new Projectile(projX, projY, 20, 10, 8, direction));
    }

    public void render(Graphics2D g2) {
        if (sprite != null) {
            g2.drawImage(sprite, x, y, width, height, null);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}