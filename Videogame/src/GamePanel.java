import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;

/**
 * Panel principal del juego que maneja la lógica y el renderizado.
 * Implementa Runnable para ejecutar el bucle del juego en un hilo separado.
 */
public class GamePanel extends JPanel implements Runnable {
    // Configuración de tamaño de tiles y pantalla
    final int original_tile_size = 16;  // Tamaño original de un tile (16x16 píxeles)
    final int scale = 3;                // Factor de escala para adaptar a resoluciones modernas

    final int tile_size = original_tile_size * scale;  // Tamaño final de un tile después de escalar
    final int width = 24;               // Número de tiles horizontales
    final int height = 18;              // Número de tiles verticales
    final int screen_width = width * tile_size;    // Ancho total de la pantalla en píxeles
    final int screen_height = height * tile_size;  // Alto total de la pantalla en píxeles

    // Componentes del juego
    KeyHandler keyH = new KeyHandler();  // Manejador de eventos de teclado
    Thread gameThread;                   // Hilo para el bucle del juego

    // Propiedades del jugador
    int player_x = 100;        // Posición X inicial del jugador
    int player_y = 500;        // Posición Y inicial del jugador
    int player_speed = 4;      // Velocidad de movimiento del jugador

    int FPS = 60;              // Fotogramas por segundo objetivo

    // Imágenes del juego
    private Image backgroundImage;  // Imagen de fondo
    private Image playerImage;      // Imagen del jugador

    // Propiedades para la física
    private final double gravity = 0.5;  // Constante de gravedad
    private double velocityY = 0;        // Velocidad vertical del jugador

    // Dirección a la que mira el jugador
    private boolean facingRight = true;  // Variable para rastrear la dirección

    /**
     * Constructor que inicializa el panel del juego y carga las imágenes.
     */
    public GamePanel() {
        this.setPreferredSize(new Dimension(screen_width, screen_height));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);  // Mejora el rendimiento de renderizado
        this.addKeyListener(keyH);     // Añade el detector de teclas al panel
        this.setFocusable(true);       // Permite que el panel reciba eventos de teclado

        // Carga las imágenes de fondo y jugador
        try {
            backgroundImage = ImageIO.read(new File("Videogame/src/assets/background.png"));
            playerImage = ImageIO.read(new File("Videogame/src/assets/player.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicia el hilo del juego para comenzar el bucle principal.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Método principal del bucle del juego.
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

    /**
     * Actualiza la lógica del juego (movimiento, colisiones, etc).
     */
    public void update() {
        int floor = screen_height - tile_size;  // Posición Y del suelo

        // Salto cuando se presiona la tecla arriba y el jugador está en el suelo
        if (keyH.upPressed && player_y + tile_size >= floor) {
            velocityY = -10;  // Fuerza de salto negativa (hacia arriba)
        }

        // Aplica la gravedad
        velocityY += gravity;
        player_y += (int) velocityY;

        // Detecta colisión con el suelo
        if (player_y + tile_size > floor) {
            player_y = floor - tile_size;  // Coloca al jugador en el suelo
            velocityY = 0;                 // Detiene la caída
        }

        // Movimiento horizontal
        if (keyH.leftPressed) {
            player_x -= player_speed;
            if (player_x < 0) {
                player_x = 0;  // Evita que el jugador salga por la izquierda
            }
            facingRight = false;  // Actualiza la dirección
        }
        if (keyH.rightPressed) {
            player_x += player_speed;
            if (player_x + tile_size > screen_width) {
                player_x = screen_width - tile_size;  // Evita que el jugador salga por la derecha
            }
            facingRight = true;   // Actualiza la dirección
        }
    }

    /**
     * Renderiza todos los elementos del juego.
     * Este método es llamado automáticamente cuando se invoca repaint().
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;  // Conversión a Graphics2D para más funcionalidades

        // Dibuja la imagen de fondo
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        // Dibuja al jugador con la orientación correcta
        if (playerImage != null) {
            AffineTransform transform = new AffineTransform();

            // Aplica la transformación según la dirección a la que mira
            if (!facingRight) {
                transform.translate(player_x + tile_size, player_y);  // Posición para imagen volteada
                transform.scale(-1, 1);  // Voltea horizontalmente
            } else {
                transform.translate(player_x, player_y);  // Posición normal
            }

            // Escala la imagen al tamaño del tile
            transform.scale((double) tile_size / playerImage.getWidth(null),
                    (double) tile_size / playerImage.getHeight(null));

            // Dibuja la imagen con las transformaciones aplicadas
            g2.drawImage(playerImage, transform, this);
        }

        g2.dispose();  // Libera recursos gráficos
    }
}