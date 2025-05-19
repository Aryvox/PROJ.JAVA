package fr.aryvoxx.projava.view;

import fr.aryvoxx.projava.model.Personnage;
import fr.aryvoxx.projava.model.StoryManager;
import fr.aryvoxx.projava.model.Chapter;
import fr.aryvoxx.projava.model.Choice;
import fr.aryvoxx.projava.model.Creature;
import fr.aryvoxx.projava.model.Item;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class GameFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Personnage personnage;
    private Color backgroundColor = new Color(32, 33, 36);
    private Color foregroundColor = new Color(232, 234, 237);
    private Color accentColor = new Color(138, 180, 248);
    private ImageIcon avatarIcon;
    private JLabel avatarLabel;
    private JProgressBar healthBar;
    private JProgressBar staminaBar;
    private JLabel gameAvatarLabel;
    private JLabel playerNameLabel;
    private static final String SAVE_FILE = "save.dat";
    private StoryManager storyManager;
    private JTextArea storyArea;
    private JPanel choicesPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel enemyPanel;
    private JLabel enemyAvatarLabel;
    private JLabel enemyNameLabel;
    private JProgressBar enemyHealthBar;
    private JProgressBar enemyStaminaBar;
    private Creature currentEnemy;
    private int combatAssautCount = 0;
    private int maxAssauts = -1;

    public GameFrame() {
        setTitle("Livre dont vous êtes le héros");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setBackground(backgroundColor);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(backgroundColor);

        createMainMenu();
        createCharacterCreation();
        createGameMenu();

        add(mainPanel);
    }

    private void createMainMenu() {
        JPanel menuPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(32, 33, 36),
                        0, getHeight(), new Color(48, 49, 52));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Style personnalisé pour les boutons
        JButton newGameButton = createStyledButton("Nouvelle Partie");
        JButton loadGameButton = createStyledButton("Charger Partie");
        JButton quitButton = createStyledButton("Quitter");

        newGameButton.addActionListener(e -> cardLayout.show(mainPanel, "CREATION"));
        loadGameButton.addActionListener(e -> loadGame());
        quitButton.addActionListener(e -> System.exit(0));

        gbc.gridy = 0;
        menuPanel.add(newGameButton, gbc);
        gbc.gridy = 1;
        menuPanel.add(loadGameButton, gbc);
        gbc.gridy = 2;
        menuPanel.add(quitButton, gbc);

        mainPanel.add(menuPanel, "MENU");
    }

    private void createCharacterCreation() {
        JPanel creationPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(32, 33, 36),
                        0, getHeight(), new Color(48, 49, 52));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Création des composants avec style
        JLabel nameLabel = new JLabel("Nom du personnage:");
        nameLabel.setForeground(foregroundColor);
        JTextField nameField = new JTextField(20);
        styleTextField(nameField);

        JButton avatarButton = createStyledButton("Choisir Avatar");
        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(150, 150));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        // Panel semi-transparent pour l'avatar
        JPanel avatarContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setColor(new Color(48, 49, 52, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        avatarContainer.setOpaque(false);
        avatarContainer.setLayout(new BorderLayout());
        avatarContainer.add(avatarLabel, BorderLayout.CENTER);

        avatarButton.addActionListener(e -> {
            AvatarSelector selector = new AvatarSelector(this);
            selector.setVisible(true);
            
            ImageIcon selectedAvatar = selector.getSelectedAvatar();
            if (selectedAvatar != null) {
                avatarIcon = selectedAvatar;
                avatarLabel.setIcon(avatarIcon);
            }
        });

        JButton createButton = createStyledButton("Créer");
        JButton backButton = createStyledButton("Retour");

        createButton.addActionListener(e -> {
            String name = nameField.getText();
            if (!name.isEmpty() && avatarIcon != null) {
                personnage = new Personnage(name);
                updateGameAvatar();
                cardLayout.show(mainPanel, "GAME");
                startGame();
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nom et choisir un avatar");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        creationPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        creationPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        creationPanel.add(avatarButton, gbc);
        
        gbc.gridx = 1;
        creationPanel.add(avatarContainer, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        creationPanel.add(createButton, gbc);
        
        gbc.gridx = 1;
        creationPanel.add(backButton, gbc);

        mainPanel.add(creationPanel, "CREATION");
    }

    private void createGameMenu() {
        if (leftPanel != null && rightPanel != null) {
            return;
        }

        JPanel gamePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(32, 33, 36),
                        0, getHeight(), new Color(48, 49, 52));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Panel gauche
        leftPanel = new JPanel(new BorderLayout(0, 20));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel des informations du joueur
        JPanel playerInfoPanel = new JPanel(new GridBagLayout());
        playerInfoPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Avatar avec fond semi-transparent
        JPanel avatarPanel = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setColor(new Color(48, 49, 52, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        avatarPanel.setOpaque(false);
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel pour l'avatar
        JPanel avatarImagePanel = new JPanel(new BorderLayout());
        avatarImagePanel.setOpaque(false);
        gameAvatarLabel = new JLabel();
        gameAvatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarImagePanel.add(gameAvatarLabel, BorderLayout.CENTER);

        // Label pour le nom du joueur
        playerNameLabel = new JLabel("", SwingConstants.CENTER);
        playerNameLabel.setForeground(foregroundColor);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        avatarPanel.add(avatarImagePanel, BorderLayout.CENTER);
        avatarPanel.add(playerNameLabel, BorderLayout.SOUTH);
        
        gbc.gridy = 0;
        playerInfoPanel.add(avatarPanel, gbc);

        // Stats avec fond semi-transparent
        JPanel statsPanel = new JPanel(new GridLayout(4, 1, 5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setColor(new Color(48, 49, 52, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel healthLabel = new JLabel("Santé");
        healthLabel.setForeground(foregroundColor);
        healthBar = createStyledProgressBar(100, new Color(255, 99, 71));
        
        JLabel staminaLabel = new JLabel("Énergie");
        staminaLabel.setForeground(foregroundColor);
        staminaBar = createStyledProgressBar(100, new Color(65, 105, 225));

        statsPanel.add(healthLabel);
        statsPanel.add(healthBar);
        statsPanel.add(staminaLabel);
        statsPanel.add(staminaBar);

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        playerInfoPanel.add(statsPanel, gbc);

        // Panel des choix (créé une seule fois)
        if (choicesPanel == null) {
            choicesPanel = new JPanel(new GridLayout(3, 1, 15, 15));
            choicesPanel.setOpaque(false);
            choicesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        }

        // Bouton de sauvegarde
        JButton saveButton = createStyledButton("Sauvegarder");
        saveButton.setPreferredSize(new Dimension(200, 30));
        saveButton.addActionListener(e -> saveGame());

        // Organisation du panel gauche
        leftPanel.add(playerInfoPanel, BorderLayout.NORTH);
        leftPanel.add(choicesPanel, BorderLayout.CENTER);
        leftPanel.add(saveButton, BorderLayout.SOUTH);

        // Zone de texte principale (créée une seule fois)
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 20));
        
        if (storyArea == null) {
            storyArea = new JTextArea();
            storyArea.setEditable(false);
            storyArea.setLineWrap(true);
            storyArea.setWrapStyleWord(true);
            storyArea.setBackground(new Color(48, 49, 52));
            storyArea.setForeground(foregroundColor);
            storyArea.setFont(new Font("Arial", Font.PLAIN, 14));
            storyArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            storyArea.setMargin(new Insets(10, 10, 10, 10));
            storyArea.setRows(20); // Définir un nombre minimum de lignes visibles
        }
        JScrollPane scrollPane = new JScrollPane(storyArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Appliquer le style custom à la scrollbar
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // --- PANEL ENNEMI ---
        enemyPanel = new JPanel();
        enemyPanel.setLayout(new BoxLayout(enemyPanel, BoxLayout.Y_AXIS));
        enemyPanel.setBackground(new Color(48, 49, 52));
        enemyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(138, 180, 248)), "Ennemi", 0, 0, new Font("Arial", Font.BOLD, 14), Color.WHITE));
        enemyPanel.setPreferredSize(new Dimension(220, 180));

        enemyAvatarLabel = new JLabel();
        enemyAvatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        enemyAvatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        enemyPanel.add(enemyAvatarLabel);

        enemyNameLabel = new JLabel("Nom de l'ennemi");
        enemyNameLabel.setForeground(Color.WHITE);
        enemyNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        enemyNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        enemyPanel.add(enemyNameLabel);

        enemyHealthBar = new JProgressBar(0, 100);
        enemyHealthBar.setForeground(new Color(255, 99, 71));
        enemyHealthBar.setBackground(new Color(32, 33, 36));
        enemyHealthBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        enemyPanel.add(enemyHealthBar);

        enemyStaminaBar = new JProgressBar(0, 100);
        enemyStaminaBar.setForeground(new Color(65, 105, 225));
        enemyStaminaBar.setBackground(new Color(32, 33, 36));
        enemyStaminaBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        enemyPanel.add(enemyStaminaBar);

        enemyPanel.setVisible(false); // caché par défaut
        rightPanel.add(enemyPanel, BorderLayout.SOUTH);
        // --- FIN PANEL ENNEMI ---

        // Ajout des panels principaux (une seule fois)
        gamePanel.add(leftPanel, BorderLayout.WEST);
        gamePanel.add(rightPanel, BorderLayout.CENTER);

        mainPanel.add(gamePanel, "GAME");
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(48, 49, 52));
        button.setForeground(foregroundColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setMaximumSize(new Dimension(200, 30));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(58, 59, 62));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(48, 49, 52));
            }
        });
        return button;
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(new Color(48, 49, 52));
        textField.setForeground(foregroundColor);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        textField.setCaretColor(foregroundColor);
    }

    private JProgressBar createStyledProgressBar(int max, Color foreground) {
        JProgressBar bar = new JProgressBar(0, max);
        bar.setValue(max);
        bar.setForeground(foreground);
        bar.setBackground(new Color(32, 33, 36));
        bar.setBorder(null);
        bar.setPreferredSize(new Dimension(200, 15));
        return bar;
    }

    private void saveGame() {
        if (personnage == null) {
            JOptionPane.showMessageDialog(this, "Aucune partie en cours à sauvegarder.");
            return;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(personnage);
            oos.writeObject(avatarIcon);
            JOptionPane.showMessageDialog(this, "Partie sauvegardée avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    private void loadGame() {
        File saveFile = new File(SAVE_FILE);
        if (!saveFile.exists()) {
            JOptionPane.showMessageDialog(this, "Aucune sauvegarde trouvée.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            personnage = (Personnage) ois.readObject();
            avatarIcon = (ImageIcon) ois.readObject();
            
            if (personnage != null && avatarIcon != null) {
                updateGameAvatar();
                updateStats();
                cardLayout.show(mainPanel, "GAME");
                startGame();
                JOptionPane.showMessageDialog(this, "Partie chargée avec succès !");
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement de la sauvegarde.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement : " + e.getMessage());
        }
    }

    private void updateGameAvatar() {
        if (gameAvatarLabel != null && avatarIcon != null && personnage != null) {
            Image scaledImage = avatarIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            gameAvatarLabel.setIcon(new ImageIcon(scaledImage));
            playerNameLabel.setText(personnage.getNom());
        }
    }

    private void updateStats() {
        if (personnage != null && healthBar != null && staminaBar != null) {
            healthBar.setValue(personnage.getPointsDeVie());
            staminaBar.setValue(personnage.getAgilite() * 10);
        }
    }

    private void startGame() {
        storyManager = new StoryManager(personnage);
        updateStory();
    }

    private void updateStory() {
        Chapter currentChapter = storyManager.getCurrentChapter();
        storyArea.setText(currentChapter.getText());
        choicesPanel.removeAll();

        // Réinitialiser le compteur d'assauts si on change de chapitre
        combatAssautCount = 0;
        maxAssauts = getMaxAssautsForChapter(currentChapter);

        if (currentChapter.requiresCombat()) {
            currentEnemy = currentChapter.getEnemy();
            updateEnemyDisplay();
            addCombatChoices();
        } else {
            currentEnemy = null;
            enemyPanel.setVisible(false);
            for (Choice choice : currentChapter.getChoices()) {
                JButton choiceButton = createStyledButton(choice.getText());
                choiceButton.addActionListener(e -> {
                    storyManager.goToChapter(choice.getNextChapter());
                    updateStory();
                });
                choicesPanel.add(choiceButton);
            }
        }
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }

    private void updateEnemyDisplay() {
        if (currentEnemy != null) {
            // Mise à jour de l'avatar
            ImageIcon enemyIcon = currentEnemy.getAvatar();
            if (enemyIcon != null) {
                Image scaledImage = enemyIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                enemyAvatarLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                enemyAvatarLabel.setIcon(null);
            }

            // Mise à jour du nom
            enemyNameLabel.setText(currentEnemy.getName());

            // Mise à jour des barres de vie et d'énergie
            enemyHealthBar.setMaximum(currentEnemy.getInitialStamina());
            enemyHealthBar.setValue(currentEnemy.getStamina());
            enemyStaminaBar.setMaximum(currentEnemy.getInitialStamina());
            enemyStaminaBar.setValue(currentEnemy.getStamina());

            // Afficher le panel ennemi
            enemyPanel.setVisible(true);
        } else {
            enemyPanel.setVisible(false);
        }
    }

    private void addCombatChoices() {
        choicesPanel.removeAll();
        
        JButton attackButton = createStyledButton("Attaquer");
        attackButton.addActionListener(e -> processCombatAction("attack"));
        
        JButton defendButton = createStyledButton("Se défendre");
        defendButton.addActionListener(e -> processCombatAction("defend"));
        
        JButton useItemButton = createStyledButton("Utiliser un objet");
        useItemButton.addActionListener(e -> showInventory());
        
        choicesPanel.add(attackButton);
        choicesPanel.add(defendButton);
        choicesPanel.add(useItemButton);
    }

    private void showInventory() {
        if (personnage.getInventaire().isEmpty()) {
            storyArea.append("\nVotre inventaire est vide !");
            return;
        }

        JDialog inventoryDialog = new JDialog(this, "Inventaire", true);
        inventoryDialog.setLayout(new BorderLayout());
        inventoryDialog.setBackground(backgroundColor);

        JPanel itemsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        itemsPanel.setBackground(backgroundColor);

        for (Item item : personnage.getInventaire()) {
            JButton itemButton = createStyledButton(item.getFullDescription());
            itemButton.addActionListener(e -> {
                personnage.utiliserItem(item);
                storyArea.append("\nVous utilisez " + item.getName() + " !");
                updateStats();
                inventoryDialog.dispose();
                processCombatAction("item");
            });
            itemsPanel.add(itemButton);
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBackground(backgroundColor);
        scrollPane.getViewport().setBackground(backgroundColor);

        inventoryDialog.add(scrollPane, BorderLayout.CENTER);
        inventoryDialog.pack();
        inventoryDialog.setLocationRelativeTo(this);
        inventoryDialog.setVisible(true);
    }

    private void processCombatAction(String action) {
        if (currentEnemy != null) {
            // Tour du joueur
            String result = storyManager.processCombatTurn(currentEnemy, true, action);
            storyArea.append("\n\n" + result);
            updateEnemyDisplay();
            updateStats();

            // Gestion spéciale pour le chapitre 4 (Triceratops)
            if (storyManager.getCurrentChapter().getId() == 4 && maxAssauts > 0) {
                combatAssautCount++;
                if (combatAssautCount >= maxAssauts) {
                    showLuckChoiceAfterAssauts();
                    return;
                }
            }

            if (!storyManager.isCombatOver(currentEnemy)) {
                // Tour de l'ennemi
                result = storyManager.processCombatTurn(currentEnemy, false, "");
                storyArea.append("\n" + result);
                updateEnemyDisplay();
                updateStats();
            }

            if (storyManager.isCombatOver(currentEnemy)) {
                if (currentEnemy.isAlive()) {
                    // Le joueur a perdu
                    storyArea.append("\n\nVous avez été vaincu !");
                    choicesPanel.removeAll();
                    JButton restartButton = createStyledButton("Recommencer");
                    restartButton.addActionListener(e -> {
                        personnage.setPointsDeVie(100);
                        storyManager.goToChapter(1);
                        updateStory();
                    });
                    choicesPanel.add(restartButton);
                } else {
                    // Le joueur a gagné
                    storyArea.append("\n\nVous avez vaincu " + currentEnemy.getName() + " !");
                    choicesPanel.removeAll();
                    // Ajout de choix après la victoire
                    JButton exploreButton = createStyledButton("Explorer les environs");
                    exploreButton.addActionListener(e -> {
                        storyManager.goToChapter(5); // Chapitre d'exploration
                        updateStory();
                    });
                    JButton restButton = createStyledButton("Se reposer");
                    restButton.addActionListener(e -> {
                        personnage.soigner(30); // Soigne 30 points de vie
                        updateStats();
                        storyArea.append("\nVous vous reposez et récupérez 30 points de vie.");
                        storyManager.goToChapter(6); // Chapitre de repos
                        updateStory();
                    });
                    choicesPanel.add(exploreButton);
                    choicesPanel.add(restButton);
                }
            }
        }
    }

    // Méthode utilitaire pour lire MAX_ASSAUTS dans le texte du chapitre
    private int getMaxAssautsForChapter(Chapter chapter) {
        String text = chapter.getText();
        for (String line : text.split("\n")) {
            if (line.trim().startsWith("MAX_ASSAUTS:")) {
                try {
                    return Integer.parseInt(line.trim().split(":")[1].trim());
                } catch (Exception e) { return -1; }
            }
        }
        return -1;
    }

    // Affiche le bouton 'Tenter votre chance' après 3 assauts
    private void showLuckChoiceAfterAssauts() {
        choicesPanel.removeAll();
        JButton luckButton = createStyledButton("Tenter votre chance");
        luckButton.addActionListener(e -> {
            storyManager.goToChapter(1004); // Numéro du chapitre de test de chance
            updateStory();
        });
        choicesPanel.add(luckButton);
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }
}

// Style personnalisé pour la scrollbar
class CustomScrollBarUI extends BasicScrollBarUI {
    private final Color thumbColor = new Color(138, 180, 248, 180);
    private final Color trackColor = new Color(48, 49, 52, 180);
    private final int arc = 10;

    @Override
    protected void configureScrollBarColors() {
        // Rien à faire ici, les couleurs sont déjà fixées par les champs final
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(thumbColor);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, arc, arc);
        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(trackColor);
        g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, arc, arc);
        g2.dispose();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }
    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
} 