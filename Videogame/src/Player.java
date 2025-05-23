import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.util.List;

// Player class represents the main controllable character in the game.
// It handles movement, animation, collision, and life management.
public class Player extends Entity {
    private final KeyHandler keyH; // Handles keyboard input for player actions
    private final Image[] runningImages = new Image[2]; // Stores images for running animation
    private int animationIndex = 0, animationCounter = 0; // Animation frame index and counter for timing
    private final int originalX, originalY; // Original spawn position for resets
    private final GamePanel gamePanel; // Reference to the main game panel
    protected boolean facingRight = true; // Indicates if the player is facing right
    private int lives = 3; // Number of lives the player has
    private long lastLifeLostTime = 0; // Frame/time when the last life was lost

    // Constructs a Player with position, size, speed, input handler, and game panel reference
    public Player(int x, int y, int width, int height, int speed, KeyHandler keyH, GamePanel gamePanel) {
        super(x, y, width, height);
        this.speed = speed;
        this.keyH = keyH;
        this.originalX = x;
        this.originalY = y;
        this.gamePanel = gamePanel;
        try {
            // Load running animation images
            runningImages[0] = ImageIO.read(new File("Videogame/src/assets/player1.png"));
            runningImages[1] = ImageIO.read(new File("Videogame/src/assets/player2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sets the number of lives
    public void setLives(int lives) { this.lives = lives; }
    // Returns the number of lives
    public int getLives() { return lives; }
    // Decreases the player's lives by one
    public void loseLife() { lives--; }
    // Checks if enough frames have passed to lose another life
    public boolean canLoseLife(int currentFrame, int cooldownFrames) {
        return (currentFrame - lastLifeLostTime) >= cooldownFrames;
    }
    // Marks the frame/time when a life was lost
    public void markLifeLost(int currentFrame) { lastLifeLostTime = currentFrame; }

    // Updates the player's state, position, and animation
    public void update(int screenHeight, List<Platform> platforms) {
        boolean onPlatform = false;
        // Predict next position for collision detection
        Rectangle playerBounds = new Rectangle(x, (int) (y + velocityY), width, height);
        // Check collision with platforms
        for (Platform p : platforms) {
            if (velocityY >= 0 && playerBounds.intersects(p.getBounds()) && y + height <= p.y + velocityY) {
                y = p.y - height;
                velocityY = 0;
                onPlatform = true;
                break;
            }
        }

        // Handle left/right movement in level 2
        if (gamePanel.getGameState() == GameState.PLAYING_LEVEL2) {
            if (keyH.leftPressed) { x -= speed; facingRight = false; }
            if (keyH.rightPressed) { x += speed; facingRight = true; }
        }

        // Handle jumping and fast fall
        if (keyH.upPressed && (y + height >= screenHeight || onPlatform)) velocityY = -15;
        if (keyH.downPressed) velocityY = 10;

        // Update position based on velocity
        super.update();

        // Prevent falling below the screen
        if (!onPlatform && y + height > screenHeight) {
            y = screenHeight - height;
            velocityY = 0;
        }

        // Update animation if moving or in certain game states
        if (gamePanel.getGameState() == GameState.PLAYING_LEVEL3 || gamePanel.getGameState() == GameState.PLAYING_LEVEL1
                || (gamePanel.getGameState() != GameState.PLAYING_LEVEL3 && (keyH.leftPressed || keyH.rightPressed))) {
            animationCounter++;
            if (animationCounter >= 10) {
                animationCounter = 0;
                animationIndex = (animationIndex + 1) % runningImages.length;
            }
        }

        // Keep player within horizontal and vertical bounds
        if (x < 0) x = 0;
        if (x + width > gamePanel.getWidth()) x = gamePanel.getWidth() - width;
        if (y < 0) y = 0;
    }

    // Renders the player using the current animation frame and direction
    public void render(Graphics2D g2) {
        if (runningImages[animationIndex] != null) {
            if (facingRight)
                g2.drawImage(runningImages[animationIndex], x, y, width, height, null);
            else
                g2.drawImage(runningImages[animationIndex], x + width, y, -width, height, null);
        }
    }

    // Returns true if the player is on the floor
    public boolean isOnFloor(int screenHeight) { return y + height >= screenHeight; }
    // Resets the player's position to the original spawn point
    public void resetPosition() { this.x = originalX; this.y = originalY; }
}
