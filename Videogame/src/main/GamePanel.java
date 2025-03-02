package main;

import javax.swing.JPanel;

public class GamePanel extends JPanel{
    final int original_tile_size = 16;
    final int scale = 4;

    final int tile_size = original_tile_size * scale;

    public GamePanel() {
        this.setPreferredSize(new Dimension (screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
    }
}
