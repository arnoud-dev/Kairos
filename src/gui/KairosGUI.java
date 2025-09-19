package gui;

import javax.swing.*;
import java.awt.event.*;

public class KairosGUI {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Kairos GUI Button Hover Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load the image for the button
        ImageIcon icon = new ImageIcon("C:\\Users\\varno\\Desktop\\Kairos\\resources\\testBtn.png");

        // Create the button with the image
        JButton button = new JButton(icon);
        button.setPreferredSize(new java.awt.Dimension(icon.getIconWidth(), icon.getIconHeight()));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        // Add action listener
        button.addActionListener(e -> {
            System.out.println("Button clicked!");
            JOptionPane.showMessageDialog(frame, "You clicked the button!");
        });

        // Add mouse listener to hide image on hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(null); // Remove the image when hovered
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(icon); // Restore the image when mouse leaves
            }
        });

        // Absolute positioning
        frame.setLayout(null);
        button.setBounds(100, 100, icon.getIconWidth(), icon.getIconHeight());
        frame.add(button);

        frame.setSize(400, 400);
        frame.setVisible(true);
    }
}
