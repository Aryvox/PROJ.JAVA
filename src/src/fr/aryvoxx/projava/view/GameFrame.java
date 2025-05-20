package fr.aryvoxx.projava.view;

import fr.aryvoxx.projava.model.Personnage;
import fr.aryvoxx.projava.model.StoryManager;
import fr.aryvoxx.projava.model.Chapter;
import fr.aryvoxx.projava.model.Choice;
import fr.aryvoxx.projava.model.Creature;
import fr.aryvoxx.projava.model.Item;
import fr.aryvoxx.projava.model.Scenario;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class GameFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel menuPanel;
    private CardLayout cardLayout;
    private Personnage personnage;
    private Color backgroundColor = new Color(32, 33, 36);
    private Color foregroundColor = new Color(232, 234, 237);
    private Color accentColor = new Color(138, 180, 248);
    private Color panelColor = new Color(48, 49, 52);
    private ImageIcon avatarIcon;
    private JLabel avatarLabel;
    private JProgressBar santeBar;
    private JProgressBar habileteBar;
    private JProgressBar enduranceBar;
    private JProgressBar chanceBar;
    private JProgressBar provisionsBar;
    private JLabel santeValueLabel;
    private JLabel habileteValueLabel;
    private JLabel enduranceValueLabel;
    private JLabel chanceValueLabel;
    private JLabel provisionsValueLabel;
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
    private JPanel characterCreationPanel;
    private JTextField nameField;
    private JComboBox<String> avatarComboBox;

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
        menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton newGameButton = createStyledButton("Nouvelle Partie");
        newGameButton.addActionListener(e -> {
            // Afficher la sélection du scénario avant la création du personnage
            showScenarioSelection();
        });

        JButton loadGameButton = createStyledButton("Charger Partie");
        loadGameButton.addActionListener(e -> loadGame());

        JButton helpButton = createStyledButton("Aide");
        helpButton.addActionListener(e -> showHelp());

        JButton editorButton = createStyledButton("Éditeur de Scénario");
        editorButton.addActionListener(e -> {
            // Créer un nouveau StoryManager avec un personnage temporaire
            Personnage tempPersonnage = new Personnage("Temp", "Guerrier");
            StoryManager storyManager = new StoryManager(tempPersonnage);
            ScenarioEditor editor = new ScenarioEditor(storyManager);
            editor.setVisible(true);
        });

        JButton quitButton = createStyledButton("Quitter");
        quitButton.addActionListener(e -> System.exit(0));

        menuPanel.add(newGameButton, gbc);
        menuPanel.add(loadGameButton, gbc);
        menuPanel.add(helpButton, gbc);
        menuPanel.add(editorButton, gbc);
        menuPanel.add(quitButton, gbc);

        mainPanel.add(menuPanel, "MENU");
        cardLayout.show(mainPanel, "MENU");
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
        nameField = new JTextField(20);
        styleTextField(nameField);

        // Type d'avatar
        JLabel avatarTypeLabel = new JLabel("Type d'avatar:");
        avatarTypeLabel.setForeground(foregroundColor);
        String[] avatarTypes = {"Guerrier", "Mage", "Archer"};
        avatarComboBox = new JComboBox<>(avatarTypes);

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
            String avatarType = (String) avatarComboBox.getSelectedItem();
            if (!name.isEmpty() && avatarIcon != null) {
                personnage = new Personnage(name, avatarType);
                showAttributeDistributionPanel();
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
        creationPanel.add(avatarTypeLabel, gbc);
        
        gbc.gridx = 1;
        creationPanel.add(avatarComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        creationPanel.add(avatarButton, gbc);
        
        gbc.gridx = 1;
        creationPanel.add(avatarContainer, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
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
        JPanel statsPanel = new JPanel(new GridLayout(5, 1, 5, 5)) {
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

        // Création des panels pour chaque statistique
        JPanel santePanel = createStatPanel("Santé", new Color(255, 0, 0));
        JPanel habiletePanel = createStatPanel("Habileté", new Color(255, 215, 0));
        JPanel endurancePanel = createStatPanel("Endurance", new Color(255, 99, 71));
        JPanel chancePanel = createStatPanel("Chance", new Color(65, 105, 225));
        JPanel provisionsPanel = createStatPanel("Provisions", new Color(50, 205, 50));

        // Ajout des panels au panel principal
        statsPanel.add(santePanel);
        statsPanel.add(habiletePanel);
        statsPanel.add(endurancePanel);
        statsPanel.add(chancePanel);
        statsPanel.add(provisionsPanel);

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
        saveButton.addActionListener(e -> saveGameWithName());

        // Bouton Accueil
        JButton homeButton = createStyledButton("Accueil");
        homeButton.setPreferredSize(new Dimension(200, 30));
        homeButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        // Organisation du panel gauche
        leftPanel.add(playerInfoPanel, BorderLayout.NORTH);
        leftPanel.add(choicesPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new GridLayout(2, 1, 10, 10));
        bottomPanel.add(saveButton);
        bottomPanel.add(homeButton);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

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
        JProgressBar bar = new JProgressBar(0, max) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dessiner la barre de progression
                int width = getWidth();
                int height = getHeight();
                
                // Calculer la progression en fonction de la valeur maximale
                double percentComplete = (double) getValue() / getMaximum();
                int progress = (int) (width * percentComplete);
                
                // Fond de la barre
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, width, height, 10, 10);
                
                // Barre de progression
                g2d.setColor(getForeground());
                g2d.fillRoundRect(0, 0, progress, height, 10, 10);
                
                // Texte de la valeur actuelle
                String text = String.valueOf(getValue());
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                
                // Centrer le texte
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int x = (width - textWidth) / 2;
                int y = (height - textHeight) / 2 + fm.getAscent();
                
                // Dessiner le texte
                g2d.drawString(text, x, y);
            }
        };
        bar.setValue(0);
        bar.setForeground(foreground);
        bar.setBackground(new Color(32, 33, 36));
        bar.setBorder(null);
        bar.setPreferredSize(new Dimension(200, 25));
        bar.setStringPainted(false);
        return bar;
    }

    private JPanel createStatPanel(String name, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);

        // Label du nom
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(foregroundColor);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Label de la valeur maximale
        JLabel maxValueLabel = new JLabel();
        maxValueLabel.setForeground(foregroundColor);
        maxValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Panel pour le nom et la valeur maximale
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        labelPanel.add(nameLabel, BorderLayout.WEST);
        labelPanel.add(maxValueLabel, BorderLayout.EAST);

        // Barre de progression
        JProgressBar progressBar = createStyledProgressBar(100, color);

        // Ajout des composants au panel
        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        // Stockage des références
        switch (name) {
            case "Santé":
                santeBar = progressBar;
                santeValueLabel = maxValueLabel;
                break;
            case "Habileté":
                habileteBar = progressBar;
                habileteValueLabel = maxValueLabel;
                break;
            case "Endurance":
                enduranceBar = progressBar;
                enduranceValueLabel = maxValueLabel;
                break;
            case "Chance":
                chanceBar = progressBar;
                chanceValueLabel = maxValueLabel;
                break;
            case "Provisions":
                provisionsBar = progressBar;
                provisionsValueLabel = maxValueLabel;
                break;
        }

        return panel;
    }

    private void saveGameWithName() {
        if (personnage == null) {
            JOptionPane.showMessageDialog(this, "Aucune partie en cours à sauvegarder.");
            return;
        }
        String name = JOptionPane.showInputDialog(this, "Nom de la sauvegarde :", "Sauvegarder la partie", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) {
            return;
        }
        String fileName = "save_" + name.trim().replaceAll("[^a-zA-Z0-9_-]", "_") + ".dat";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(personnage);
            oos.writeObject(avatarIcon);
            // Sauvegarder le chapitre courant
            if (storyManager != null && storyManager.getCurrentChapter() != null) {
                oos.writeInt(storyManager.getCurrentChapter().getId());
            } else {
                oos.writeInt(1); // par défaut chapitre 1
            }
            JOptionPane.showMessageDialog(this, "Partie sauvegardée sous '" + fileName + "' !");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    private void loadGame() {
        File dir = new File(".");
        File[] saves = dir.listFiles((d, name) -> name.startsWith("save_") && name.endsWith(".dat"));
        if (saves == null || saves.length == 0) {
            JOptionPane.showMessageDialog(this, "Aucune sauvegarde trouvée.");
            return;
        }
        String[] options = new String[saves.length];
        for (int i = 0; i < saves.length; i++) {
            options[i] = saves[i].getName().replaceFirst("save_", "").replaceFirst("\\.dat$", "");
        }
        String selected = (String) JOptionPane.showInputDialog(this, "Choisissez une sauvegarde :", "Charger Partie", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (selected == null) return;
        String fileName = "save_" + selected + ".dat";
        File saveFile = new File(fileName);
        if (!saveFile.exists()) {
            JOptionPane.showMessageDialog(this, "Sauvegarde introuvable.");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            personnage = (Personnage) ois.readObject();
            avatarIcon = (ImageIcon) ois.readObject();
            int chapitreId = 1;
            try {
                chapitreId = ois.readInt();
            } catch (Exception ex) {
                chapitreId = 1;
            }
            if (personnage != null && avatarIcon != null) {
                updateGameAvatar();
                updateStats();
                cardLayout.show(mainPanel, "GAME");
                startGame();
                // Aller au bon chapitre après le chargement
                if (storyManager != null) {
                    storyManager.goToChapter(chapitreId);
                    updateStory();
                }
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

    public void updateStats() {
        if (personnage != null) {
            // Mise à jour des barres de progression avec les valeurs exactes
            int santeMax = personnage.getSanteInitiale();
            int santeActuelle = personnage.getSante();
            santeBar.setMaximum(santeMax);
            santeBar.setValue(santeActuelle);
            
            int habileteMax = personnage.getHabileteInitiale();
            int habileteActuelle = personnage.getHabilete();
            habileteBar.setMaximum(habileteMax);
            habileteBar.setValue(habileteActuelle);
            
            int enduranceMax = personnage.getEnduranceInitiale();
            int enduranceActuelle = personnage.getEndurance();
            enduranceBar.setMaximum(enduranceMax);
            enduranceBar.setValue(enduranceActuelle);
            
            int chanceMax = personnage.getChanceInitiale();
            int chanceActuelle = personnage.getChance();
            chanceBar.setMaximum(chanceMax);
            chanceBar.setValue(chanceActuelle);
            
            int provisionsMax = 10;
            int provisionsActuelle = personnage.getProvisions();
            provisionsBar.setMaximum(provisionsMax);
            provisionsBar.setValue(provisionsActuelle);

            // Mise à jour des labels de valeurs maximales
            santeValueLabel.setText(String.format("/%d", santeMax));
            habileteValueLabel.setText(String.format("/%d", habileteMax));
            enduranceValueLabel.setText(String.format("/%d", enduranceMax));
            chanceValueLabel.setText(String.format("/%d", chanceMax));
            provisionsValueLabel.setText(String.format("/%d", provisionsMax));

            // Forcer la mise à jour visuelle des barres
            santeBar.repaint();
            habileteBar.repaint();
            enduranceBar.repaint();
            chanceBar.repaint();
            provisionsBar.repaint();
            
            // Print pour vérifier les valeurs lues
            System.out.println("Updating stats UI:");
            System.out.println("Santé: " + santeActuelle + "/" + santeMax);
            System.out.println("Habileté: " + habileteActuelle + "/" + habileteMax);
            System.out.println("Endurance: " + enduranceActuelle + "/" + enduranceMax);
            System.out.println("Chance: " + chanceActuelle + "/" + chanceMax);
            System.out.println("Provisions: " + provisionsActuelle + "/" + provisionsMax);
        }
    }

    public void showMainMenu() {
        cardLayout.show(mainPanel, "MENU");
    }

    private void startGame() {
        if (storyManager == null) {
            // Si aucun scénario n'est chargé, demander à l'utilisateur d'en choisir un
            showScenarioSelection();
            return;
        }
        
        // Mettre à jour le personnage dans le StoryManager
        storyManager.setPersonnage(personnage);
        personnage.setGameFrame(this);
        updateStory();
        updateStats();
        cardLayout.show(mainPanel, "GAME");
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

    private void showHelp() {
        String doc = "Bienvenue dans le jeu !\n\n" +
                "But du jeu :\n" +
                "- Parcourez l'aventure, faites des choix, combattez des ennemis et tentez votre chance.\n" +
                "- Chaque chapitre propose des choix qui influencent la suite de l'histoire.\n" +
                "- Les combats se déroulent en tour par tour.\n" +
                "- Utilisez votre inventaire pour vous soigner ou gagner des bonus.\n\n" +
                "Commandes :\n" +
                "- Cliquez sur les boutons de choix pour avancer.\n" +
                "- Utilisez le bouton 'Sauvegarder' pour enregistrer votre progression.\n" +
                "- Utilisez le bouton 'Inventaire' en combat pour utiliser un objet.\n\n" +
                "Conseils :\n" +
                "- Lisez bien chaque texte avant de choisir.\n" +
                "- Certains choix peuvent avoir des conséquences inattendues !\n\n" +
                "Bonne aventure !";
        JOptionPane.showMessageDialog(this, doc, "Aide - Comment jouer", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createCharacterCreationPanel() {
        characterCreationPanel = new JPanel(new GridBagLayout());
        characterCreationPanel.setBackground(new Color(0, 0, 0, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Titre
        JLabel titleLabel = new JLabel("Création du Personnage");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        characterCreationPanel.add(titleLabel, gbc);

        // Nom du personnage
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        characterCreationPanel.add(new JLabel("Nom:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        characterCreationPanel.add(nameField, gbc);

        // Type d'avatar
        gbc.gridx = 0;
        gbc.gridy = 2;
        characterCreationPanel.add(new JLabel("Type d'avatar:"), gbc);
        String[] avatarTypes = {"Guerrier", "Mage", "Archer"};
        avatarComboBox = new JComboBox<>(avatarTypes);
        gbc.gridx = 1;
        characterCreationPanel.add(avatarComboBox, gbc);

        // Bouton de création
        JButton createButton = new JButton("Créer le personnage");
        createButton.addActionListener(e -> {
            String nom = nameField.getText();
            String avatarType = (String) avatarComboBox.getSelectedItem();
            if (!nom.isEmpty()) {
                personnage = new Personnage(nom, avatarType);
                showAttributeDistributionPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nom pour votre personnage.");
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        characterCreationPanel.add(createButton, gbc);
    }

    private void showAttributeDistributionPanel() {
        JPanel distributionPanel = new JPanel(new GridBagLayout()) {
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

        // Titre
        JLabel titleLabel = new JLabel("Distribution des Points");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        distributionPanel.add(titleLabel, gbc);

        // Points disponibles
        JLabel pointsLabel = new JLabel("Points disponibles: " + personnage.getPointsDisponibles());
        pointsLabel.setForeground(Color.WHITE);
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 1;
        distributionPanel.add(pointsLabel, gbc);

        // Panel pour les attributs avec fond semi-transparent
        JPanel attributesPanel = new JPanel(new GridLayout(4, 3, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setColor(new Color(48, 49, 52, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        attributesPanel.setOpaque(false);
        attributesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Attributs
        String[] attributes = {"Habileté", "Endurance", "Chance", "Santé"};
        JButton[] plusButtons = new JButton[4];
        JLabel[] valueLabels = new JLabel[4];

        for (int i = 0; i < attributes.length; i++) {
            final int index = i;
            
            // Label de l'attribut
            JLabel attrLabel = new JLabel(attributes[i]);
            attrLabel.setForeground(Color.WHITE);
            attrLabel.setFont(new Font("Arial", Font.BOLD, 14));
            attributesPanel.add(attrLabel);

            // Valeur actuelle
            valueLabels[i] = new JLabel(getAttributeValue(i));
            valueLabels[i].setForeground(Color.WHITE);
            valueLabels[i].setFont(new Font("Arial", Font.PLAIN, 14));
            attributesPanel.add(valueLabels[i]);

            // Bouton +
            plusButtons[i] = createStyledButton("+");
            plusButtons[i].setPreferredSize(new Dimension(40, 30));
            plusButtons[i].addActionListener(e -> {
                if (personnage.getPointsDisponibles() > 0) {
                    boolean success = false;
                    switch (index) {
                        case 0: success = personnage.augmenterHabilete(); break;
                        case 1: success = personnage.augmenterEndurance(); break;
                        case 2: success = personnage.augmenterChance(); break;
                        case 3: success = personnage.augmenterSante(); break;
                    }
                    if (success) {
                        pointsLabel.setText("Points disponibles: " + personnage.getPointsDisponibles());
                        valueLabels[index].setText(getAttributeValue(index));
                        if (personnage.getPointsDisponibles() == 0) {
                            for (JButton button : plusButtons) {
                                button.setEnabled(false);
                            }
                        }
                    }
                }
            });
            attributesPanel.add(plusButtons[i]);
        }

        gbc.gridy = 2;
        gbc.gridwidth = 3;
        distributionPanel.add(attributesPanel, gbc);

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        // Bouton de confirmation
        JButton confirmButton = createStyledButton("Commencer l'aventure");
        confirmButton.setPreferredSize(new Dimension(200, 40));
        confirmButton.addActionListener(e -> {
            if (personnage.getPointsDisponibles() == 0) {
                // Log values before updating UI
                System.out.println("Values before starting game and updating UI:");
                System.out.println("Santé: " + personnage.getSante());
                System.out.println("Habileté: " + personnage.getHabilete());
                System.out.println("Endurance: " + personnage.getEndurance());
                System.out.println("Chance: " + personnage.getChance());
                System.out.println("Provisions: " + personnage.getProvisions());

                updateGameAvatar();
                mainPanel.add(distributionPanel, "DISTRIBUTION");
                cardLayout.show(mainPanel, "GAME");
                startGame();
                updateStats(); // Ensure stats are updated right after showing game view
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez utiliser tous vos points avant de continuer.",
                    "Points restants",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        // Bouton retour
        JButton backButton = createStyledButton("Retour");
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "CREATION"));

        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);

        gbc.gridy = 3;
        distributionPanel.add(buttonPanel, gbc);

        // Remplacer le panneau actuel
        mainPanel.add(distributionPanel, "DISTRIBUTION");
        cardLayout.show(mainPanel, "DISTRIBUTION");
        revalidate();
        repaint();
    }

    private String getAttributeValue(int index) {
        switch (index) {
            case 0: return String.valueOf(personnage.getHabilete());
            case 1: return String.valueOf(personnage.getEndurance());
            case 2: return String.valueOf(personnage.getChance());
            case 3: return String.valueOf(personnage.getSante());
            default: return "";
        }
    }

    private void showScenarioSelection() {
        // Créer un panel pour la sélection du scénario
        JPanel scenarioPanel = new JPanel(new GridBagLayout());
        scenarioPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Titre
        JLabel titleLabel = new JLabel("Choisissez un scénario");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(foregroundColor);
        scenarioPanel.add(titleLabel, gbc);

        // Liste des scénarios
        JList<String> scenarioList = new JList<>();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        
        // Chercher tous les fichiers .txt dans le répertoire
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        
        if (files != null) {
            for (File file : files) {
                listModel.addElement(file.getName());
            }
        }

        scenarioList.setModel(listModel);
        scenarioList.setBackground(panelColor);
        scenarioList.setForeground(foregroundColor);
        scenarioList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(scenarioList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scenarioPanel.add(scrollPane, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(backgroundColor);

        JButton selectButton = createStyledButton("Sélectionner");
        selectButton.addActionListener(e -> {
            String selectedScenario = scenarioList.getSelectedValue();
            if (selectedScenario != null) {
                try {
                    // Créer un StoryManager avec le scénario sélectionné
                    Personnage tempPersonnage = new Personnage("Temp", "Guerrier");
                    storyManager = new StoryManager(tempPersonnage);
                    storyManager.setCurrentScenario(Scenario.loadFromFile(selectedScenario));
                    
                    // Passer à la création du personnage
                    cardLayout.show(mainPanel, "CREATION");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement du scénario : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un scénario",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton backButton = createStyledButton("Retour");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        buttonPanel.add(selectButton);
        buttonPanel.add(backButton);
        scenarioPanel.add(buttonPanel, gbc);

        // Ajouter le panel au CardLayout
        mainPanel.add(scenarioPanel, "SCENARIO_SELECTION");
        cardLayout.show(mainPanel, "SCENARIO_SELECTION");
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
