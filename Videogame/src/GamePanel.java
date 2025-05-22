// At the top of GamePanel.java
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Panel principal del juego que maneja la lógica y el renderizado.
 * Implementa Runnable para ejecutar el bucle del juego en un hilo separado.
 */
public class GamePanel extends JPanel implements Runnable {
    // Configuración de tamaño de tiles y pantalla
    final int original_tile_size = 20;  // Tamaño original de un tile (20x20 píxeles)
    final int scale = 3;                // Factor de escala para adaptar a resoluciones modernas

    final int tile_size = original_tile_size * scale;  // Tamaño final de un tile después de escalar
    final int width = 6 * scale;               // Número de tiles horizontales
    final int height = 4 * scale;              // Número de tiles verticales
    final int screen_width = width * tile_size;    // Ancho total de la pantalla en píxeles
    final int screen_height = height * tile_size;  // Alto total de la pantalla en píxeles

    GameState currGameState = GameState.MENU;

    // Componentes del juego
    KeyHandler keyH = new KeyHandler(this); // Manejador de eventos de teclado
    Thread gameThread;                   // Hilo para el bucle del juego
    Player player;                       // Jugador
    int FPS = 60;              // Fotogramas por segundo objetivo

    // Imágenes del juego
    private Image level1Image;  // Imagen de fondo
    private Image level2Image;  // Imagen de fondo
    private Image menuImage;        // Imagen del menú
    private Image pauseImage;      // Imagen de pausa
    private Image gameOverImage;   // Imagen de Game Over
    private Image winImage;        // Imagen de victoria
    private final Image[] lifeBarImages = new Image[3];


    // Estado del juego
    private GameState gameState = GameState.MENU;

    // Lista de plataformas
    private final List<Platform> platforms = new ArrayList<>();
    private final Random random = new Random();
    private int platformSpawnCounter = 0;
    private int platformSpawnInterval = 90;

    AudioPlayer audioPlayer = new AudioPlayer();

    private final List<Enemy> enemies = new ArrayList<>();
    private int platformsSinceLastEnemy = 0;

    // Botones del menú
    private final Rectangle playMenuButton = new Rectangle(340, 200, 400, 130);
    private final Rectangle exitMenuButton = new Rectangle(340, 450, 400, 100);
    private final Rectangle helpMenuButton = new Rectangle(0, 0, screen_width, screen_height);

    // Botones del pausa
    private final Rectangle playPauseButton = new Rectangle(320, 250, 440, 90);
    private final Rectangle exitPauseButton = new Rectangle(320, 410, 440, 90);
    private final Rectangle helpPauseButton = new Rectangle(320, 410, 440, 90);

    // Botones del game over
    private final Rectangle replayButton = new Rectangle(200, 460, 660, 110);
    private final Rectangle menuGOverButton = new Rectangle(200, 300, 660, 110);
    private final Rectangle exitGOverButton = new Rectangle(340, 590, 370, 60);

    // Botones de win
    private final Rectangle winButton = new Rectangle(0, 0, screen_width, screen_height);

    // Constructor del panel del juego
    public GamePanel() {
        this.setPreferredSize(new Dimension(screen_width, screen_height));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);  // Mejora el rendimiento de renderizado
        this.addKeyListener(keyH);     // Añade el detector de teclas al panel
        this.setFocusable(true);
        keyH = new KeyHandler(this);// Permite que el panel reciba eventos de teclado
        this.addKeyListener(keyH); // Añade el manejador de teclas
        // Inicializa al jugador
        player = new Player(200, 500, tile_size, tile_size, 4, keyH, this);
        player.loadImage("Videogame/src/assets/player.png");

        // Carga la imagen de fondo
        try {
            level1Image = ImageIO.read(new File("Videogame/src/assets/level3.png"));
            level2Image = ImageIO.read(new File("Videogame/src/assets/level2.png"));
            pauseImage = ImageIO.read(new File("Videogame/src/assets/pause.png"));
            menuImage = ImageIO.read(new File("Videogame/src/assets/menu.png"));
            gameOverImage = ImageIO.read(new File("Videogame/src/assets/game_over.png"));
            winImage = ImageIO.read(new File("Videogame/src/assets/win.png"));
            lifeBarImages[0] = ImageIO.read(new File("Videogame/src/assets/healthBar1.png"));
            lifeBarImages[1] = ImageIO.read(new File("Videogame/src/assets/healthBar2.png"));
            lifeBarImages[2] = ImageIO.read(new File("Videogame/src/assets/healthBar3.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
//                Menu
                if (gameState == GameState.MENU){
                    if (playMenuButton.contains(p)) {
                        resetLevel();
                        setGameState(GameState.PLAYING_LEVEL2);
                    } else if (exitMenuButton.contains(p)) {
                        System.exit(0);
                    }
                }
//                Pausa
                if (gameState == GameState.PAUSED) {
                    if (playPauseButton.contains(p)) {
                        setGameState(currGameState);
                    } else if (exitPauseButton.contains(p)) {
                        System.exit(0);
                    }
//                    Game Over
                } else if (gameState == GameState.GAME_OVER) {
                    if (replayButton.contains(p)) {
                        resetLevel();
                        setGameState(currGameState);
                    } else if (exitGOverButton.contains(p)) {
                        System.exit(0);
                    } else if (menuGOverButton.contains(p)) {
                        resetLevel();
                        setGameState(GameState.MENU);
                    }
//                    Win
                } else if (gameState == GameState.WIN) {
                    if (winButton.contains(p)) {
                        setGameState(GameState.MENU);
                        resetLevel();
                    }
                }
            }
        });
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Add these methods to GamePanel
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void run() {
        double draw_interval = 1000000000.0 / FPS;  // Nanosegundos por fotograma
        double delta = 0;
        long last_time = System.nanoTime();
        long current_time;
        long timer = 0;
        int draw_count = 0;

        // Bucle principal del juego
        while (gameThread.isAlive()) {
            current_time = System.nanoTime();
            delta += (current_time - last_time) / draw_interval;
            timer += current_time - last_time;
            last_time = current_time;

            // Actualiza y renderiza cuando es tiempo de un nuevo fotograma
            if (delta >= 1) {
                if (gameState == GameState.PLAYING_LEVEL3 || gameState == GameState.PLAYING_LEVEL2) {
                    update();   // Only update game logic if playing
                }
                repaint();      // Always repaint to show menu or pause screen
                delta--;
                draw_count++;
            }

            // Muestra los FPS cada segundo
            if (timer >= 1000000000) {
                System.out.println("FPS: " + draw_count);
                draw_count = 0;
                timer = 0;
            }
        }
    }

    // Variables para el temporizador de nivel
    private final int levelTimeLimit = 30 * FPS;
    private int levelTimer = levelTimeLimit; // in frames

    private int level2EnemySpawnCounter = 0;


    // Variables para la velocidad de la plataforma
    private double platformSpeed = 4.0;

    // Posición del fondo
    private int backgroundX = 0;

    int platWidth = tile_size * 2;
    int platHeight = tile_size / 2;

    // Actualiza la lógica del juego (movimiento, colisiones, etc).
    boolean startLevel3 = true;

    private int globalFrameCounter = 0;


    public void update() {
        // Actualiza la posición del fondo para simular el auto-scroll
        // Velocidad del auto-scroll
        globalFrameCounter++;
        player.update(screen_height, platforms);

        // frames (2 seconds at 60 FPS)
        int level2EnemySpawnInterval = 120;
        switch (gameState) {
            case PLAYING_LEVEL3:
            if (startLevel3) {
                platforms.add(new Platform(0, 650, 2 * platWidth, platHeight, 1));
                backgroundX -= (int) platformSpeed;
                startLevel3 = false;
                audioPlayer.playBackgroundMusic("Videogame/src/assets/audio3.wav");
            }

            // Reinicia la posición del fondo cuando salga completamente de la pantalla
            if (backgroundX <= -screen_width) {
                backgroundX = 0;
            }

            // Actualiza la posición de las plataformas
            // Set your desired Y range
            int minY = 450; // minimum Y position
            int maxY = 600; // maximum Y position

            // When spawning a platform:
            int maxPlatformSpawnInterval = 60;
            // minimum spawn interval
            platformSpawnCounter++;
            if (platformSpawnCounter >= platformSpawnInterval) {
                if (platformSpawnInterval > maxPlatformSpawnInterval) {
                    // Reduce the spawn interval to increase difficulty
                    platformSpawnInterval -= 5;
                }
                platformSpawnCounter = 0;
                // Spawn a new platform
                int platY = random.nextInt(maxY - minY + 1) + minY;
                platforms.add(new Platform(screen_width, platY, platWidth, platHeight, 0));
                platformsSinceLastEnemy++;

            }

            for (Platform p : platforms) {
                if (p.type == 1) {
                    p.x -= (int) platformSpeed / 4; // Move floor slower
                } else {
                    p.x -= (int) platformSpeed;
                }
            }

            platforms.removeIf(p -> p.x + p.width < 0);

            // Actualiza al jugador
            if (player.isOnFloor(screen_height)) {
                // Player has fallen off the screen
                audioPlayer.stop();
                gameState = GameState.GAME_OVER;
            }

            double maxPlatformSpeed = 20.0;
            if (platformSpeed < maxPlatformSpeed) {
                // Adjust for desired acceleration
                double increment = 0.01;
                platformSpeed += increment;
            }

            break;
            case PLAYING_LEVEL2:
                // Example: spawn an enemy every interval with 50% chance
                level2EnemySpawnCounter++;
                if (level2EnemySpawnCounter >= level2EnemySpawnInterval) {
                    level2EnemySpawnCounter = 0;
                    int enemyX = random.nextInt(screen_width - tile_size);
                    int floorY = screen_height - 2 * tile_size; // Align with floor
                    enemies.add(new Enemy(enemyX, floorY, tile_size, tile_size));
                }
                // Update existing enemies if needed
                for (Enemy e : enemies) {
                    e.update();
                }
                int cooldownFrames = 120; // 2 seconds at 60 FPS
                for (Enemy e : enemies) {
                    if (player.getBounds().intersects(e.getBounds())) {
                        e.setHit(true);
                        if (player.canLoseLife(globalFrameCounter, cooldownFrames)) {
                            player.loseLife();
                            player.markLifeLost(globalFrameCounter);
                            if (player.getLives() < 0) {
                                currGameState = gameState;
                                setGameState(GameState.GAME_OVER);
                            }
                        }
                    }
                }

                enemies.removeIf(Enemy::isDead);

            break;
        }

        if (gameState == GameState.PLAYING_LEVEL3 || gameState == GameState.PLAYING_LEVEL2) {
            levelTimer--;
            if (levelTimer <= 0) {
                switch (gameState) {
                    case PLAYING_LEVEL2:
                        setGameState(GameState.PLAYING_LEVEL3);
                        currGameState = GameState.PLAYING_LEVEL3;
                        break;
                    case PLAYING_LEVEL3:
                        setGameState(GameState.WIN);
                        currGameState = GameState.WIN;
                        break;
                }
                resetLevel();
            }
        }

        if(keyH.escapePressed) {
            currGameState = gameState;
            gameState = GameState.PAUSED;
        }

        if (gameState == GameState.GAME_OVER) {
//            resetLevel();
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int seconds = levelTimer / FPS;
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 255, 255));
        switch (gameState) {
            case MENU:
                g2.drawImage(menuImage, backgroundX, 0, this.getWidth(), this.getHeight(), this);
                // g2.fill(playMenuButton);
                // g2.fill(exitMenuButton);
                break;
            case PAUSED:
                g2.drawImage(pauseImage, backgroundX, 0, this.getWidth(), this.getHeight(), this);
                // g2.fill(exitPauseButton);
                // g2.fill(playPauseButton);
                break;
            case GAME_OVER:
                g2.drawImage(gameOverImage, backgroundX, 0, this.getWidth(), this.getHeight(), this);
                // g2.fill(exitGOverButton);
//                 g2.fill(replayButton);
                // g2.fill(menuGOverButton);
                break;
            case WIN:
                g2.drawImage(winImage, backgroundX, 0, this.getWidth(), this.getHeight(), this);
                break;
            case PLAYING_LEVEL3:
                g2.drawImage(level1Image, backgroundX, 0, this.getWidth(), this.getHeight(), this);
                for (Platform p : platforms) {
                    p.render(g2);
                }
                // Draw timer (top left corner)
                g2.setFont(new Font("Cascadia Code", Font.BOLD, 32));
                g2.setColor(new Color(76, 187, 23));
                g2.drawString("Time left: " + seconds + "s", 30, 50);
                // Dibuja al jugador
                player.render(g2);

                for (Enemy e : enemies) {
                    e.render(g2);
                }

                break;
            case PLAYING_LEVEL2:
                g2.drawImage(level2Image, backgroundX, 0, this.getWidth(), this.getHeight(), this);
                player.render(g2);
                g2.setFont(new Font("Cascadia Code", Font.BOLD, 32));
                g2.setColor(new Color(255, 87, 23));
                g2.drawString("Time left: " + seconds + "s", 30, 50);

//            Platform healthBar = new Platform(20, 20, platWidth, platHeight, 1);
//            healthBar.render(g2);

                for (Enemy e : enemies) {
                    e.setPlayer(player);
                    e.render(g2);
                }

                int lives = player.getLives();
                lives = Math.max(0, Math.min(lives, 2)); // Clamp between 0 and 3
                g2.drawImage(lifeBarImages[lives], 850, -50, 2*platWidth, 6*platHeight, this);

                break;
        }
        g2.dispose();
    }

    // Reset or load level-specific data
    private void resetLevel() {
        platforms.clear();
        enemies.clear();
        player.setLives(3);
        player.resetPosition();
        player.facingRight = true;
        levelTimer = levelTimeLimit;
        startLevel3 = true;
        audioPlayer.stop();
        platformSpawnCounter = 0;
        platformsSinceLastEnemy = 0;
        platformSpeed = 4.0;
        platformSpawnInterval = 90;
        backgroundX = 0;
    }

}