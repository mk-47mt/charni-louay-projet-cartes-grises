package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fenêtre principale avec un design moderne inspiré de l'automobile.
 */
public class MainView extends JFrame {
    public MainView() {
        setTitle("Gestion Automobile");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuration du panneau principal avec fond stylisé
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("34822-wheel-mercedes_amg-mercedes_benz_c_class-mercedes_benz_c_63_amg-alloy_wheel-3680x2456.jpg");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        
        // Style des boutons inspiré des interfaces futuristes
        Font buttonFont = new Font("Roboto", Font.BOLD, 18);
        Color buttonColor = new Color(50, 50, 50);
        Color textColor = Color.WHITE;
        
        JButton marqueButton = createStyledButton("Gérer les marques", "icon_marque.png", buttonFont, buttonColor, textColor);
        marqueButton.addActionListener(e -> openView(new MarqueView()));
        gbc.gridy = 0;
        panel.add(marqueButton, gbc);

        JButton modeleButton = createStyledButton("Gérer les modèles", "icon_modele.png", buttonFont, buttonColor, textColor);
        modeleButton.addActionListener(e -> openView(new ModeleView()));
        gbc.gridy = 1;
        panel.add(modeleButton, gbc);

        JButton proprietaireButton = createStyledButton("Gérer les propriétaires", "icon_proprietaire.png", buttonFont, buttonColor, textColor);
        proprietaireButton.addActionListener(e -> openView(new ProprietaireView()));
        gbc.gridy = 2;
        panel.add(proprietaireButton, gbc);

        JButton vehiculeButton = createStyledButton("Gérer les véhicules", "icon_vehicule.png", buttonFont, buttonColor, textColor);
        vehiculeButton.addActionListener(e -> openView(new VehiculeView()));
        gbc.gridy = 3;
        panel.add(vehiculeButton, gbc);

        JButton possederButton = createStyledButton("Gérer les propriétés", "icon_posseder.png", buttonFont, buttonColor, textColor);
        possederButton.addActionListener(e -> openView(new PossederView()));
        gbc.gridy = 4;
        panel.add(possederButton, gbc);

        add(panel);
        setVisible(true);
    }

    private JButton createStyledButton(String text, String iconPath, Font font, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 60));
        
        ImageIcon icon = new ImageIcon(iconPath);
        button.setIcon(icon);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        
        animateBorder(button);
        return button;
    }

    private void animateBorder(JButton button) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int r = 255, g = 0, b = 0;
            int step = 15;
            
            @Override
            public void run() {
                if (r == 255 && g < 255 && b == 0) g += step;
                else if (g == 255 && r > 0) r -= step;
                else if (g == 255 && b < 255) b += step;
                else if (b == 255 && g > 0) g -= step;
                else if (b == 255 && r < 255) r += step;
                else if (r == 255 && b > 0) b -= step;

                button.setBorder(BorderFactory.createLineBorder(new Color(r, g, b), 3));
                button.repaint();
            }
        }, 0, 100);
    }

    private void openView(JFrame view) {
        try {
            view.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Erreur lors de l'ouverture de la vue : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new MainView();
    }
}