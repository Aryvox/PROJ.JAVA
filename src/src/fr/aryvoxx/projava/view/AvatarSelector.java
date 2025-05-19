package fr.aryvoxx.projava.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class AvatarSelector extends JDialog {
    private ImageIcon selectedAvatar = null;
    private final Map<String, ImageIcon> avatars = new HashMap<>();
    private final String[] avatarNames = {
        "Knight", "Druid", "Assasin", "Priest", "Elf"
    };

    public AvatarSelector(JFrame parent) {
        super(parent, "Sélection de l'Avatar", true);
        setLayout(new BorderLayout());
        initializeAvatars();
        createUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeAvatars() {
        // Création d'avatars temporaires avec des couleurs différentes
        Color[] colors = {
            new Color(255, 100, 100),  // Rouge pour Knight
            new Color(100, 100, 255),  // Bleu pour Druid
            new Color(100, 255, 100),  // Vert pour Assasin
            new Color(255, 255, 100),  // Jaune pour Priest
            new Color(255, 100, 255),  // Magenta pour Elf
        };

        for (int i = 0; i < avatarNames.length; i++) {
            try {
                // Essayer de charger l'image depuis le dossier resources
                String imagePath = "src/resources/avatars/" + avatarNames[i].toLowerCase() + ".png";
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    BufferedImage img = ImageIO.read(imageFile);
                    avatars.put(avatarNames[i], new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                } else {
                    // Si l'image n'existe pas, créer un avatar temporaire
                    BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = img.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Dessiner un cercle avec la couleur correspondante
                    g2d.setColor(colors[i]);
                    g2d.fillOval(10, 10, 80, 80);
                    
                    // Ajouter une bordure
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawOval(10, 10, 80, 80);
                    
                    // Ajouter les initiales du personnage
                    g2d.setFont(new Font("Arial", Font.BOLD, 32));
                    g2d.setColor(Color.WHITE);
                    String initial = avatarNames[i].substring(0, 1);
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = 50 - fm.stringWidth(initial) / 2;
                    int y = 50 + fm.getHeight() / 3;
                    g2d.drawString(initial, x, y);
                    
                    g2d.dispose();
                    avatars.put(avatarNames[i], new ImageIcon(img));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(32, 33, 36));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel pour les avatars
        JPanel avatarsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        avatarsPanel.setBackground(new Color(32, 33, 36));

        for (String avatarName : avatarNames) {
            JPanel avatarContainer = new JPanel(new BorderLayout());
            avatarContainer.setBackground(new Color(48, 49, 52));
            avatarContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JButton avatarButton = new JButton(avatars.get(avatarName));
            avatarButton.setPreferredSize(new Dimension(120, 120));
            avatarButton.setBackground(new Color(48, 49, 52));
            avatarButton.setBorder(BorderFactory.createEmptyBorder());
            
            JLabel nameLabel = new JLabel(avatarName, SwingConstants.CENTER);
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            avatarButton.addActionListener(e -> {
                selectedAvatar = avatars.get(avatarName);
                dispose();
            });

            avatarButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    avatarContainer.setBackground(new Color(58, 59, 62));
                    avatarButton.setBackground(new Color(58, 59, 62));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    avatarContainer.setBackground(new Color(48, 49, 52));
                    avatarButton.setBackground(new Color(48, 49, 52));
                }
            });

            avatarContainer.add(avatarButton, BorderLayout.CENTER);
            avatarContainer.add(nameLabel, BorderLayout.SOUTH);
            avatarsPanel.add(avatarContainer);
        }

        mainPanel.add(avatarsPanel, BorderLayout.CENTER);

        // Bouton Annuler
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setBackground(new Color(48, 49, 52));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cancelButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(32, 33, 36));
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setPreferredSize(new Dimension(450, 400));
    }

    public ImageIcon getSelectedAvatar() {
        return selectedAvatar;
    }
} 