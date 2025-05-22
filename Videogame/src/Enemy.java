import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends Entity {
    private final List<Projectile> projectiles = new ArrayList<>();
    private Player player;
    private final Image[] runningImages = new Image[12]; // Imágenes para la animación
    private int animationIndex = 0; // Índice de la imagen actual
    private boolean useFirstSprite = true;
    private int animationCounter = 0;
    private int lifetime = 0;
    private boolean isDead = false;
    private boolean isHit = false;
    private int hitAnimationCounter = 0;
    private int hitAnimationIndex = 0;


    public Enemy(int x, int y, int width, int height) {
        super(x, y, width, height);
        try {
            runningImages[0] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy1.png"));
            runningImages[1] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy2.png"));
            runningImages[2] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy3.png"));
            runningImages[3] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy4.png"));
            runningImages[4] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy5.png"));
            runningImages[5] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy6.png"));
            runningImages[6] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy7.png"));
            runningImages[7] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy8.png"));
            runningImages[8] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy9.png"));
            runningImages[9] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy10.png"));
            runningImages[10] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy11.png"));
            runningImages[11] = ImageIO.read(new File("Videogame/src/assets/enemy/enemy12.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        if (isHit) {
            hitAnimationCounter++;
            int hitAnimationSpeed = 2;
            if (hitAnimationCounter >= hitAnimationSpeed) {
                hitAnimationCounter = 0;
                hitAnimationIndex++;
                if (hitAnimationIndex >= runningImages.length) {
                    hitAnimationIndex = 0;
                    isHit = false; // End hit animation
                }
            }
        } else {
            if (player != null) {
                // Adjust as needed
                int speed = 2;
                if (player.x < this.x) {
                    this.x -= speed / 2;
                } else if (player.x > this.x) {
                    this.x += speed / 2;
                }
            }

            // Update position
            animationCounter++;
            int animationSpeed = 10;
            if (animationCounter >= animationSpeed) {
                animationCounter = 0;
                animationIndex = (animationIndex + 1) % runningImages.length;
            }
            lifetime++;
            if (lifetime >= 180) {
                isDead = true;
            }
        }
    }

    public void setHit(boolean hit) {
        this.isHit = hit;
        this.hitAnimationIndex = 0;
        this.hitAnimationCounter = 0;
    }

    public boolean isDead() {
        return isDead;
    }

    public void render(Graphics2D g2) {
        int imgIdx = isHit ? hitAnimationIndex : 0; // Always sprite 0 if not hit
        if (runningImages[imgIdx] != null) {
            if (player != null && player.x < this.x) {
                g2.drawImage(runningImages[imgIdx], x + width, y, -width, height, null);
            } else {
                g2.drawImage(runningImages[imgIdx], x, y, width, height, null);
            }
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isUseFirstSprite() {
        return useFirstSprite;
    }

    public void setUseFirstSprite(boolean useFirstSprite) {
        this.useFirstSprite = useFirstSprite;
    }

    @Override
    public Rectangle getBounds() {
        int insetX = width / 6;   // Adjust as needed
        int insetY = height / 6;  // Adjust as needed
        int w = width - 2 * insetX;
        int h = height - 2 * insetY;
        return new Rectangle(x + insetX, y + insetY, w, h);
    }

}