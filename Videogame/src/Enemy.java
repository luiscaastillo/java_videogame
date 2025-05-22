import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends Entity {
    private final List<Projectile> projectiles = new ArrayList<>();
    private Player player;
    private final Image[] runningImages = new Image[2]; // Imágenes para la animación
    private int animationIndex = 0; // Índice de la imagen actual
    private boolean useFirstSprite = true;
    private int animationCounter = 0;
    private int lifetime = 0;
    private boolean isDead = false;


    private int shootCounter = 0;

    private int speed = 2; // Adjust as needed

    public Enemy(int x, int y, int width, int height) {
        super(x, y, width, height);
        try {
            runningImages[0] = ImageIO.read(new File("Videogame/src/assets/enemy.png"));
            runningImages[1] = ImageIO.read(new File("Videogame/src/assets/enemy.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        if (player != null) {
            if (player.x < this.x) {
                this.x -= speed/2;
            } else if (player.x > this.x) {
                this.x += speed/2;
            }
        }

        // Update position
        animationCounter++;
        int animationSpeed = 10;
        if (animationCounter >= animationSpeed) {
            animationCounter = 0;
            animationIndex = (animationIndex + 1) % runningImages.length; // Alterna entre 0 y 1
        }

        lifetime++;
        if (lifetime >= 180) {
            isDead = true;
        }

    }

    public boolean isDead() {
        return isDead;
    }

    public void render(Graphics2D g2) {
        if (runningImages[animationIndex] != null) {
            if (player != null && player.x < this.x) {
                g2.drawImage(runningImages[animationIndex], x, y, width, height, null);
            } else {
                g2.drawImage(runningImages[animationIndex], x + width, y, -width, height, null);
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
}