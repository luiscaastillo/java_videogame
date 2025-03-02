package main;

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{
    final int original_tile_size = 16;
    final int scale = 3;

    final int tile_size = original_tile_size * scale;
    final int width = 16;
    final int height = 12;
    final int screen_width = width * tile_size;
    final int screen_height = height * tile_size;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;

    int player_x = 100;
    int player_y = 100;
    int player_speed = 4;

    int FPS = 60;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screen_width, screen_height));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double draw_interval =  1000000000.0 / FPS;;
        double delta = 0;
        long last_time = System.nanoTime();
        long current_time;
        long timer = 0;
        int draw_count = 0;

        while (gameThread.isAlive()) {

            current_time = System.nanoTime();

            delta += (current_time - last_time) / draw_interval;
            timer += current_time - last_time;

            last_time = current_time;

            if (delta >= 1) {
                update();
                repaint();

                delta--;
                draw_count++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + draw_count);
                draw_count = 0;
                timer = 0;
            }

        }
    }
    public void update() {
        if ( keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) {
                player_y -= player_speed;
            }
            if (keyH.downPressed) {
                player_y += player_speed;
            }
            if (keyH.leftPressed) {
                player_x -= player_speed;
            }
            if (keyH.rightPressed) {
                player_x += player_speed;
            }
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.WHITE);
        g2.fillRect(player_x, player_y, tile_size, tile_size);

        g2.dispose();
    }

}
