import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Platform extends Entity {
    private static Image platformImage;
    protected final int type;

    static {
        try {
            platformImage = ImageIO.read(new File("Videogame/src/assets/platform.png"));
//            healthBarImage = ImageIO.read(new File("Videogame/src/assets/healthBar.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Constructor
    public Platform(int x, int y, int width, int height, int type) {
        super(x, y, width, height);
        this.type = type;
    }

    // Constructor para plataformas heathBar
    public void render(Graphics2D g2) {
        Image img = platformImage;
        if (img != null) {
            int stretchW = 150; // pixels to stretch horizontally (total)
            int stretchH = 40; // pixels to stretch vertically (upwards)
            int drawX = x - stretchW / 2;
            int drawY = y - stretchH;
            int drawWidth = width + stretchW;
            int drawHeight = height + stretchH;
            g2.drawImage(img, drawX, drawY, drawWidth, drawHeight, null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width - 20, height - 50);
    }
}