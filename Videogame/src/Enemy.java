import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Enemy extends Entity {
    // Reference to the player for movement logic.
    private Player player;
    // Animation frames for the enemy.
    private final Image[] runningImages = new Image[12];
    // Animation state variables.
    private int animationIndex = 0;
    private int animationCounter = 0;
    private int lifetime = 0;
    private boolean isDead = false;
    private boolean isHit = false;
    private int hitAnimationCounter = 0;
    private int hitAnimationIndex = 0;

    // Constructs an enemy at the given position and size.
    public Enemy(int x, int y, int width, int height) {
        super(x, y, width, height);
        try {
            // Load enemy animation images.
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

    // Updates the enemy's state and animation.
    @Override
    public void update() {
        if (isHit) {
            // Play hit animation.
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
                // Move towards the player.
                int speed = 1;
                if (player.x < this.x) {
                    this.x -= speed;
                } else if (player.x > this.x) {
                    this.x += speed;
                }
            }
            // Update animation and lifetime.
            animationCounter++;
            int animationSpeed = 10;
            if (animationCounter >= animationSpeed) {
                animationCounter = 0;
                animationIndex = (animationIndex + 1) % runningImages.length;
            }
            // Update lifetime and check if the enemy should die.
            lifetime++;
            if (lifetime >= 180) isDead = true;
        }
    }

    // Sets the enemy to hit state for animation.
    public void setHit(boolean hit) {
        this.isHit = hit;
        this.hitAnimationIndex = 0;
        this.hitAnimationCounter = 0;
    }

    // Returns whether the enemy should be removed.
    public boolean isDead() {return isDead;}

    // Renders the enemy on the screen.
    public void render(Graphics2D g2) {
        int imgIdx = isHit ? hitAnimationIndex : 0;
        if (runningImages[imgIdx] != null) {
            if (player != null && player.x < this.x) g2.drawImage(runningImages[imgIdx], x + width, y, -width, height, null);
            else g2.drawImage(runningImages[imgIdx], x, y, width, height, null);
        }
    }

    // Sets the player reference for movement logic.
    public void setPlayer(Player player) {this.player = player;}
}
