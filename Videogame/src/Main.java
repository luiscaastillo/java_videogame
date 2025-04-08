import javax.swing.JFrame;

/**
 * Clase principal que inicia la aplicación del juego.
 * Configura la ventana principal y carga el panel del juego.
 */
public class Main {
    public static void main(String[] args) {
        // Crear la ventana principal
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Cierra la aplicación al cerrar la ventana
        window.setResizable(false);  // Evita que el usuario cambie el tamaño de la ventana
        window.setTitle("DE REGRESO DEL MICTLÁN");  // Establece el título de la ventana
        window.setSize(1280, 960);  // Establece el tamaño inicial de la ventana

        // Crear y añadir el panel del juego a la ventana
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        // Ajusta el tamaño de la ventana para adaptarse al tamaño preferido del panel
        window.pack();

        // Centra la ventana en la pantalla
        window.setLocationRelativeTo(null);
        // Hace visible la ventana
        window.setVisible(true);

        // Inicia el hilo del juego
        gamePanel.startGameThread();
    }
}