import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.util.List;

public class Player extends Entity {
    private final KeyHandler keyH;
    private final Image[] runningImages = new Image[2]; // Imágenes para la animación
    private int animationIndex = 0; // Índice de la imagen actual
    private int animationCounter = 0; // Contador para controlar la velocidad de animación
    private final int originalX;
    private final int originalY;
    private final GamePanel gamePanel;
    protected boolean facingRight = true;
    private int lives;


    public Player(int x, int y, int width, int height, int speed, KeyHandler keyH, GamePanel gamePanel) {
        super(x, y, width, height);
        this.speed = speed;
        this.keyH = keyH;
        this.originalX = x;
        this.originalY = y;
        this.gamePanel = gamePanel;
        this.lives = 3; // Inicializa las vidas del jugador
        // Carga las imágenes de animación
        try {
            runningImages[0] = ImageIO.read(new File("Videogame/src/assets/player1.png"));
            runningImages[1] = ImageIO.read(new File("Videogame/src/assets/player2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    private long lastLifeLostTime = 0; // in frames

    public boolean canLoseLife(int currentFrame, int cooldownFrames) {
        return (currentFrame - lastLifeLostTime) >= cooldownFrames;
    }

    public void markLifeLost(int currentFrame) {
        lastLifeLostTime = currentFrame;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
    }
    public void update(int screenHeight, List<Platform> platforms) {

        // Platform collision
        boolean onPlatform = false;
        Rectangle playerBounds = new Rectangle(x, (int) (y + velocityY), width, height);
        for (Platform p : platforms) {
            Rectangle platBounds = p.getBounds();
            // Check if player is falling and lands on top of the platform
            if (velocityY >= 0 && playerBounds.intersects(platBounds) && y + height <= p.y + velocityY) {
                y = p.y - height;
                velocityY = 0;
                onPlatform = true;
                break;
            }
        }

        if (gamePanel.getGameState() != GameState.PLAYING_LEVEL3) {
            // Move left
            if (keyH.leftPressed) {
                x -= speed;
            }
            // Move right
            if (keyH.rightPressed) {
                x += speed;
            }
        }

        // Jump
        if (keyH.upPressed && (y + height >= screenHeight ||onPlatform)) {
            velocityY = -15;
        }

        if (keyH.downPressed) {
            velocityY = 10;
        }

        // Gravity
        super.update();


        // Floor collision if not on any platform
        if (!onPlatform && y + height> screenHeight) {
            y = screenHeight - height;
            velocityY = 0;
        }

        if (gamePanel.getGameState() == GameState.PLAYING_LEVEL3) {
            // Actualiza la animación
            animationCounter++;
            // Velocidad de cambio de imagen
            int animationSpeed = 10;
            if (animationCounter >= animationSpeed) {
                animationCounter = 0;
                animationIndex = (animationIndex + 1) % runningImages.length; // Alterna entre 0 y 1
            }
        }

        if (gamePanel.getGameState() != GameState.PLAYING_LEVEL3) {
            if (keyH.leftPressed) {
                x -= speed;
                facingRight = false;
            }
            if (keyH.rightPressed) {
                x += speed;
                facingRight = true;
            }
        }

        // Animation only when moving left or right
        if (gamePanel.getGameState() != GameState.PLAYING_LEVEL3 && (keyH.leftPressed || keyH.rightPressed)) {
            animationCounter++;
            int animationSpeed = 10;
            if (animationCounter >= animationSpeed) {
                animationCounter = 0;
                animationIndex = (animationIndex + 1) % runningImages.length;
            }
        }

        if (x < 0) x = 0;
        if (x + width > gamePanel.getWidth()) x = gamePanel.getWidth() - width;

        // Prevent player from going above the screen
        if (y < 0) y = 0;
    }

    public void render(Graphics2D g2) {
        // Dibuja la imagen actual de la animación
        if (runningImages[animationIndex] != null) {
            if (facingRight) {
                g2.drawImage(runningImages[animationIndex], x, y, width, height, null);
            } else {
                // Flip horizontally
                g2.drawImage(runningImages[animationIndex], x + width, y, -width, height, null);
            }
        }
    }

    public boolean isOnFloor(int screenHeight) {
        return y + height >= screenHeight;
    }

    public void resetPosition() {
        this.x = originalX;
        this.y = originalY;
    }

}