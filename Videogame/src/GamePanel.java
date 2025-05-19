// At the top of GamePanel.java
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

    // Componentes del juego
    KeyHandler keyH = new KeyHandler();  // Manejador de eventos de teclado
    Thread gameThread;                   // Hilo para el bucle del juego
    Player player;                       // Jugador

    int FPS = 60;              // Fotogramas por segundo objetivo

    // Imágenes del juego
    private Image backgroundImage;  // Imagen de fondo

    // Lista de plataformas
    private List<Platform> platforms = new ArrayList<>();
    private Random random = new Random();
    private int platformSpawnCounter = 0;
    private int platformSpawnInterval = 90; // frames (1.5 seconds at 60 FPS)

    // Constructor del panel del juego
    public GamePanel() {
        this.setPreferredSize(new Dimension(screen_width, screen_height));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);  // Mejora el rendimiento de renderizado
        this.addKeyListener(keyH);     // Añade el detector de teclas al panel
        this.setFocusable(true);       // Permite que el panel reciba eventos de teclado

        // Inicializa al jugador
        player = new Player(200, 500, tile_size, tile_size, 4, keyH);
        player.loadImage("Videogame/src/assets/player.png");

        // Carga la imagen de fondo
        try {
            backgroundImage = ImageIO.read(new File("Videogame/src/assets/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    /**
     * Inicia el hilo del juego para comenzar el bucle principal.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Métod0 principal del bucle del juego.
     * Mantiene el juego actualizado y renderizado a una tasa constante.
     */
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
                update();       // Actualiza la lógica del juego
                repaint();      // Renderiza el juego
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

    // Posición del fondo
    private int backgroundX = 0;

    // Actualiza la lógica del juego (movimiento, colisiones, etc).
    public void update() {
        // Actualiza la posición del fondo para simular el auto-scroll
        // Velocidad del auto-scrollx
        int backgroundSpeed = 4;
        backgroundX -= backgroundSpeed;

        // Reinicia la posición del fondo cuando salga completamente de la pantalla
        if (backgroundX <= -screen_width) {
            backgroundX = 0;
        }

        // Actualiza la posición de las plataformas
        // Set your desired Y range
        int minY = 300; // minimum Y position
        int maxY = 550; // maximum Y position

        // When spawning a platform:

        platformSpawnCounter++;
        if (platformSpawnCounter >= platformSpawnInterval) {
            platformSpawnCounter = 0;
            // Spawn a new platform
            int platWidth = tile_size * 3;
            int platHeight = tile_size / 2;
            int platX = screen_width + random.nextInt(200);
            int platY = random.nextInt(maxY - minY + 1) + minY;
            platforms.add(new Platform(platX, platY, platWidth, platHeight));
        }

        for (Platform p : platforms) {
            p.x -= backgroundSpeed; // match background speed
        }

        platforms.removeIf(p -> p.x + p.width < 0);

        // Actualiza al jugador
        player.update(screen_height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Dibuja el fondo desplazado
        if (backgroundImage != null) {
            // Dibuja dos copias del fondo para simular un bucle continuo
            g2.drawImage(backgroundImage, backgroundX, 0, this.getWidth(), this.getHeight(), this);
            g2.drawImage(backgroundImage, backgroundX + screen_width, 0, this.getWidth(), this.getHeight(), this);
        }

        for (Platform p : platforms) {
            p.render(g2);
        }

        // Dibuja al jugador
        player.render(g2);
        g2.dispose();
    }

}