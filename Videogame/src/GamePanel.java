import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

// GamePanel is the main game surface, handling game logic, rendering, and input.
public class GamePanel extends JPanel implements Runnable {
    // Tile size and screen dimensions
    final int tileSize = 60;
    final int screenWidth = 18 * tileSize;
    final int screenHeight = 12 * tileSize;
    final int FPS = 60;

    // Tracks the current and previous game states
    GameState currGameState = GameState.MENU;
    private GameState gameState = GameState.MENU;

    // Handles keyboard input
    KeyHandler keyH = new KeyHandler(this);
    // Main game thread
    Thread gameThread;
    // The player character
    Player player;
    // List of platforms and enemies in the game
    private final List<Platform> platforms = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    // Random number generator for spawning
    private final Random random = new Random();

    // Images for backgrounds, UI, and life bar
    private Image level1Image, level2Image, level3Image, help, menuImage, pauseImage, gameOverImage, winImage;
    private final Image[] lifeBarImages = new Image[3];

    // Platform and enemy spawn control variables
    private int platformSpawnTick = 0, platformSpawnInterval = 90;
    private int enemySpawnCounter = 0;
    private double platformSpeed = 4.0;
    private int backgroundX = 0;
    private final int platWidth = tileSize * 2;
    private final int platHeight = tileSize / 2;
    // Level timer and time limit
    private int levelTimer;
    private final int levelTimeLimit = 10 * FPS;
    // Flag for starting level 3
    private boolean startPlatformLvl = true;
    // Global frame counter for timing
    private int globalFrameCounter = 0;

    // Rectangles for clickable UI buttons
    private final Rectangle playButtonInMenu = new Rectangle(340, 200, 400, 130);
    private final Rectangle exitButtonInMenu = new Rectangle(340, 450, 400, 100);
    private final Rectangle helpButtonInMenu = new Rectangle(340, 330, 400, 130);
    private final Rectangle playButtonInPause = new Rectangle(320, 250, 440, 90);
    private final Rectangle exitButtonInPause = new Rectangle(320, 410, 440, 90);
    private final Rectangle helpButtonInPause = new Rectangle(320, 570, 440, 90);
    private final Rectangle replayButtonInGameOver = new Rectangle(200, 460, 660, 110);
    private final Rectangle menuButtonInGameOver = new Rectangle(200, 300, 660, 110);
    private final Rectangle exitButtonInGameOver = new Rectangle(340, 590, 370, 60);
    private final Rectangle winButtonInWin = new Rectangle(0, 0, screenWidth, screenHeight);
    private final Rectangle exitButtonInHelp = new Rectangle(0, 0, screenWidth, screenHeight);

    // Handles background music and sound effects
    AudioPlayer audioPlayer = new AudioPlayer();

    // Constructor initializes the panel, loads resources, and sets up input
    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);

        player = new Player(200, 500, tileSize, tileSize, 4, keyH, this);
        player.loadImage("Videogame/src/assets/player.png");

        try {
            // Load all required images
            level1Image = ImageIO.read(new File("Videogame/src/assets/level1.png"));
            level2Image = ImageIO.read(new File("Videogame/src/assets/level2.png"));
            level3Image = ImageIO.read(new File("Videogame/src/assets/level3.png"));
            pauseImage = ImageIO.read(new File("Videogame/src/assets/pause.png"));
            menuImage = ImageIO.read(new File("Videogame/src/assets/menu.png"));
            gameOverImage = ImageIO.read(new File("Videogame/src/assets/game_over.png"));
            winImage = ImageIO.read(new File("Videogame/src/assets/win.png"));
            help = ImageIO.read(new File("Videogame/src/assets/help.png"));
            lifeBarImages[0] = ImageIO.read(new File("Videogame/src/assets/healthBar1.png"));
            lifeBarImages[1] = ImageIO.read(new File("Videogame/src/assets/healthBar2.png"));
            lifeBarImages[2] = ImageIO.read(new File("Videogame/src/assets/healthBar3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mouse listener for handling button clicks in different game states
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                if (gameState == GameState.MENU) {
                    if (playButtonInMenu.contains(p)) {
                        resetLevel();
                        setGameState(GameState.PLAYING_LEVEL1);
                        currGameState = gameState;
                    } else if (helpButtonInMenu.contains(p)) {
                        setGameState(GameState.HELP);
                        currGameState = gameState;
                    } else if (exitButtonInMenu.contains(p)) {
                        System.exit(0);
                    }
                } else if (gameState == GameState.PAUSED) {
                    if (playButtonInPause.contains(p)) setGameState(currGameState);
                    else if (exitButtonInPause.contains(p)) System.exit(0);
                    else if (helpButtonInPause.contains(p)) setGameState(GameState.HELP);
                } else if (gameState == GameState.GAME_OVER) {
                    if (replayButtonInGameOver.contains(p)) {
                        resetLevel();
                        setGameState(currGameState);
                    } else if (exitButtonInGameOver.contains(p)) System.exit(0);
                    else if (menuButtonInGameOver.contains(p)) {
                        resetLevel();
                        setGameState(GameState.MENU);
                        currGameState = gameState;
                    }
                } else if (gameState == GameState.WIN) {
                    if (winButtonInWin.contains(p)) {
                        setGameState(GameState.MENU);
                        resetLevel();
                    }
                } else if (gameState == GameState.HELP) {
                    if (exitButtonInHelp.contains(p)) setGameState(currGameState);
                }
            }
        });

        levelTimer = levelTimeLimit;
    }

    // Starts the main game loop in a new thread
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Returns the current game state
    public GameState getGameState() { return gameState; }

    // Sets the game state and handles background music changes
    public void setGameState(GameState state) {
        gameState = state;
        audioPlayer.stop();
        switch (state) {
            case PLAYING_LEVEL1 -> audioPlayer.playBackgroundMusic("Videogame/src/assets/audio2.wav");
            case PLAYING_LEVEL2 -> audioPlayer.playBackgroundMusic("Videogame/src/assets/audio1.wav");
            case PLAYING_LEVEL3 -> audioPlayer.playBackgroundMusic("Videogame/src/assets/audio3.wav");
        }
    }

    // Main game loop, handles timing and calls update/repaint
    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS, delta = 0;
        long lastTime = System.nanoTime(), timer = 0;
        int drawCount = 0;
        while (gameThread.isAlive()) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;
            if (delta >= 1) {
                if (gameState == GameState.PLAYING_LEVEL1 || gameState == GameState.PLAYING_LEVEL2
                    || gameState == GameState.PLAYING_LEVEL3)
                    update();
                repaint();
                delta--;
                drawCount++;
            }
            if (timer >= 1_000_000_000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    // Updates game logic based on the current state
    public void update() {
        // Update player and game state
        currGameState = gameState;
        globalFrameCounter++;
        player.update(screenHeight, platforms);

        // Handle pausing
        if (keyH.escapePressed) {
            currGameState = gameState;
            gameState = GameState.PAUSED;
        }

        int enemySpawnInterval = 120;
        // Handle platform spawning and movement
        switch (gameState) {
            case PLAYING_LEVEL1, PLAYING_LEVEL3 -> {
                if (startPlatformLvl) {
                    platforms.add(new Platform(0, 650, 2 * platWidth, platHeight, 1));
                    backgroundX -= (int) platformSpeed;
                    startPlatformLvl = false;
                }
                if (backgroundX <= -screenWidth) backgroundX = 0;
                int minY = 450, maxY = 600, maxPlatformSpawnInterval = 60;
                platformSpawnTick++;
                if (platformSpawnTick >= platformSpawnInterval) {
                    if (platformSpawnInterval > maxPlatformSpawnInterval) platformSpawnInterval -= 5;
                    platformSpawnTick = 0;
                    int platY = random.nextInt(maxY - minY + 1) + minY;
                    platforms.add(new Platform(screenWidth, platY, platWidth, platHeight, 0));
                }
                for (Platform p : platforms)
                    p.x -= (p.type == 1) ? (int) platformSpeed / 4 : (int) platformSpeed;
                platforms.removeIf(p -> p.x + p.width < 0);
                if (player.isOnFloor(screenHeight)) setGameState(GameState.GAME_OVER);
                if (platformSpeed < 20.0) platformSpeed += 0.01;
            }
            case PLAYING_LEVEL2 -> {
                enemySpawnCounter++;
                if (enemySpawnCounter >= enemySpawnInterval) {
                    enemySpawnCounter = 0;
                    int enemyX = random.nextInt(screenWidth - tileSize);
                    int floorY = screenHeight - 2 * tileSize;
                    enemies.add(new Enemy(enemyX, floorY, tileSize, tileSize));
                }
                for (Enemy e : enemies) e.update();
                int cooldownFrames = 150;
                for (Enemy e : enemies) {
                    if (player.getBounds().intersects(e.getBounds())) {
                        e.setHit(true);
                        if (player.canLoseLife(globalFrameCounter, cooldownFrames)) {
                            player.loseLife();
                            player.markLifeLost(globalFrameCounter);
                            if (player.getLives() < 0) {
                                setGameState(GameState.GAME_OVER);
                            }
                        }
                    }
                }
                enemies.removeIf(Enemy::isDead);
            }
            case GAME_OVER, WIN -> resetLevel();
        }

        // Handle level timer and transitions
        if (gameState == GameState.PLAYING_LEVEL1 || gameState == GameState.PLAYING_LEVEL2
            || gameState == GameState.PLAYING_LEVEL3) {
            levelTimer--;
            if (levelTimer <= 0) {
                switch (gameState) {
                    // Transition to the next level or win state
                    case PLAYING_LEVEL1 -> {
                        platforms.clear();
                        player.resetPosition();
                        setGameState(GameState.PLAYING_LEVEL2);
                    }
                    case PLAYING_LEVEL2 -> {
                        player.setLives(3);
                        player.resetPosition();
                        startPlatformLvl = true;
                        player.facingRight = true;
                        enemies.clear();
                        setGameState(GameState.PLAYING_LEVEL3);
                    }
                    case PLAYING_LEVEL3 -> setGameState(GameState.WIN);
                }
                levelTimer = levelTimeLimit;
                return;
            }
        }
        System.out.println(gameState);
    }

    // Renders the game based on the current state
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int seconds = levelTimer / FPS;
        Graphics2D g2 = (Graphics2D) g;
        switch (gameState) {
            case MENU -> g2.drawImage(menuImage, backgroundX, 0, getWidth(), getHeight(), this);
            case PAUSED -> g2.drawImage(pauseImage, backgroundX, 0, getWidth(), getHeight(), this);
            case GAME_OVER -> g2.drawImage(gameOverImage, backgroundX, 0, getWidth(), getHeight(), this);
            case WIN -> g2.drawImage(winImage, backgroundX, 0, getWidth(), getHeight(), this);
            case PLAYING_LEVEL1, PLAYING_LEVEL3 -> {
                g2.drawImage(gameState == GameState.PLAYING_LEVEL1 ?
                        level1Image :
                        level3Image, backgroundX, 0, getWidth(), getHeight(), this);
                for (Platform p : platforms) p.render(g2);
                g2.setFont(new Font("Cascadia Code", Font.BOLD, 32));
                g2.setColor(gameState == GameState.PLAYING_LEVEL1 ?
                        new Color(241, 196, 15) :
                        new Color(76, 187, 23));
                g2.drawString("Time left: " + seconds + "s", 30, 50);
                player.render(g2);
                for (Enemy e : enemies) e.render(g2);
            }
            case PLAYING_LEVEL2 -> {
                g2.drawImage(level2Image, backgroundX, 0, getWidth(), getHeight(), this);
                player.render(g2);
                g2.setFont(new Font("Cascadia Code", Font.BOLD, 32));
                g2.setColor(new Color(255, 87, 23));
                g2.drawString("Time left: " + seconds + "s", 30, 50);
                for (Enemy e : enemies) {
                    e.setPlayer(player);
                    e.render(g2);
                }
                int lives = Math.max(0, Math.min(player.getLives(), 2));
                g2.drawImage(lifeBarImages[lives],
                        850,
                        -50,
                        2 * platWidth,
                        6 * platHeight,
                        this);
            }
            case HELP -> {
                g2.drawImage(help, backgroundX, 0, getWidth(), getHeight(), this);
                String[] lines = {
                    "Para jugar, usa las teclas de A y D para moverte.",
                    "Presiona W para saltar.",
                    "Presiona S para caer (Niveles 1 y 3).",
                    "Evita a los enemigos y sobrevive el tiempo necesario",
                    "¡Buena suerte!"
                };
                g2.setFont(new Font("Cascadia Code", Font.BOLD, 32));
                FontMetrics fm = g2.getFontMetrics();
                int lineHeight = fm.getHeight();
                int totalTextHeight = lines.length * lineHeight;
                int y = (getHeight() - totalTextHeight) / 2 + fm.getAscent();
                for (String line : lines) {
                    int textWidth = fm.stringWidth(line);
                    int x = (getWidth() - textWidth) / 2;
                    g2.setColor(new Color(17, 122, 101));
                    g2.drawString(line, x + 2, y + 2);
                    g2.setColor(new Color(26, 188, 156));
                    g2.drawString(line, x, y);
                    y += lineHeight;
                }
            }
        }
        g2.dispose();
    }

    // Resets the game state and entities for a new level
    private void resetLevel() {
        // Clear platforms and enemies
        platforms.clear();
        enemies.clear();
        // Reset player state
        player.setLives(3);
        player.resetPosition();
        player.facingRight = true;
        // Reset level timer and platform state
        levelTimer = levelTimeLimit;
        startPlatformLvl = true;
        audioPlayer.stop();
        // Reset platform and background state
        platformSpawnTick = 0;
        platformSpeed = 4.0;
        platformSpawnInterval = 90;
        backgroundX = 0;
    }
}
