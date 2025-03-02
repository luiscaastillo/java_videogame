package main;

import javax.swing.JFrame;;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle("Test");
        window.setSize(1280, 960);
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

}

