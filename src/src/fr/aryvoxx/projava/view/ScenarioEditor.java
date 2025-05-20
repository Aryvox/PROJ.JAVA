package fr.aryvoxx.projava.view;

import fr.aryvoxx.projava.model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class ScenarioEditor extends JFrame {
    private JPanel mainPanel;
    private JList<Chapter> chapterList;
    private DefaultListModel<Chapter> chapterListModel;
    private JTextArea chapterTextArea;
    private JPanel choicesPanel;
    private JPanel statsPanel;
    private JPanel enemyPanel;
    private Chapter currentChapter;
    private StoryManager storyManager;
    
    // Couleurs pour le thème sombre
    private Color backgroundColor = new Color(32, 33, 36);    // Gris très foncé
    private Color foregroundColor = new Color(232, 234, 237); // Gris très clair
    private Color accentColor = new Color(138, 180, 248);     // Bleu clair
    private Color panelColor = new Color(48, 49, 52);         // Gris foncé
    private Color borderColor = new Color(60, 64, 67);        // Gris moyen
    private Color successColor = new Color(46, 204, 113);     // Vert
    private Color dangerColor = new Color(231, 76, 60);       // Rouge

    public ScenarioEditor(StoryManager storyManager) {
        this.storyManager = storyManager;
        setTitle("Éditeur de Scénario");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setBackground(backgroundColor);

        initializeComponents();
        loadChapters();
    }

    private void initializeComponents() {
        // Appliquer le thème sombre à tous les composants Swing
        UIManager.put("Panel.background", panelColor);
        UIManager.put("Panel.foreground", foregroundColor);
        UIManager.put("Button.background", accentColor);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("TextField.background", panelColor);
        UIManager.put("TextField.foreground", foregroundColor);
        UIManager.put("TextField.caretForeground", foregroundColor);
        UIManager.put("TextArea.background", panelColor);
        UIManager.put("TextArea.foreground", foregroundColor);
        UIManager.put("TextArea.caretForeground", foregroundColor);
        UIManager.put("List.background", panelColor);
        UIManager.put("List.foreground", foregroundColor);
        UIManager.put("List.selectionBackground", accentColor);
        UIManager.put("List.selectionForeground", Color.WHITE);
        UIManager.put("ScrollPane.background", panelColor);
        UIManager.put("ScrollPane.foreground", foregroundColor);
        UIManager.put("ScrollBar.background", panelColor);
        UIManager.put("ScrollBar.foreground", foregroundColor);
        UIManager.put("ScrollBar.thumb", accentColor);
        UIManager.put("TabbedPane.background", panelColor);
        UIManager.put("TabbedPane.foreground", foregroundColor);
        UIManager.put("TabbedPane.selected", accentColor);
        UIManager.put("Label.background", panelColor);
        UIManager.put("Label.foreground", foregroundColor);
        UIManager.put("CheckBox.background", panelColor);
        UIManager.put("CheckBox.foreground", foregroundColor);
        UIManager.put("Spinner.background", panelColor);
        UIManager.put("Spinner.foreground", foregroundColor);
        UIManager.put("Spinner.arrowButtonBackground", accentColor);
        UIManager.put("Spinner.arrowButtonForeground", Color.WHITE);
        UIManager.put("Separator.background", borderColor);
        UIManager.put("Separator.foreground", borderColor);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel gauche avec la liste des chapitres
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(panelColor);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.setPreferredSize(new Dimension(200, 0));

        // Barre d'outils pour les chapitres
        JPanel chapterToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chapterToolbar.setBackground(panelColor);
        
        JButton newChapterButton = createStyledButton("➕ Nouveau Chapitre");
        newChapterButton.setPreferredSize(new Dimension(150, 30));
        newChapterButton.addActionListener(e -> createNewChapter());
        
        JButton deleteChapterButton = createStyledButton("🗑️ Supprimer");
        deleteChapterButton.setPreferredSize(new Dimension(120, 30));
        deleteChapterButton.addActionListener(e -> deleteSelectedChapter());
        
        JTextField searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(150, 30));
        searchField.setBackground(panelColor);
        searchField.setForeground(foregroundColor);
        searchField.setCaretColor(foregroundColor);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterChapters(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { filterChapters(searchField.getText()); }
            public void insertUpdate(DocumentEvent e) { filterChapters(searchField.getText()); }
        });

        chapterToolbar.add(newChapterButton);
        chapterToolbar.add(deleteChapterButton);
        chapterToolbar.add(new JLabel("🔍"));
        chapterToolbar.add(searchField);

        // Liste des chapitres
        chapterListModel = new DefaultListModel<>();
        chapterList = new JList<>(chapterListModel);
        chapterList.setBackground(panelColor);
        chapterList.setForeground(foregroundColor);
        chapterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chapterList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBackground(isSelected ? accentColor : panelColor);
                label.setForeground(isSelected ? Color.WHITE : foregroundColor);
                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return label;
            }
        });
        chapterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Chapter selected = chapterList.getSelectedValue();
                if (selected != null) {
                    loadChapter(selected);
                }
            }
        });

        JScrollPane chapterScrollPane = new JScrollPane(chapterList);
        chapterScrollPane.setPreferredSize(new Dimension(200, 0));
        chapterScrollPane.setBackground(panelColor);
        chapterScrollPane.setBorder(BorderFactory.createLineBorder(borderColor));

        leftPanel.add(chapterToolbar, BorderLayout.NORTH);
        leftPanel.add(chapterScrollPane, BorderLayout.CENTER);

        // Panel central avec le texte du chapitre
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(panelColor);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel chapterTextLabel = new JLabel("Texte du chapitre :");
        chapterTextLabel.setForeground(foregroundColor);
        centerPanel.add(chapterTextLabel, BorderLayout.NORTH);

        chapterTextArea = new JTextArea();
        chapterTextArea.setBackground(panelColor);
        chapterTextArea.setForeground(foregroundColor);
        chapterTextArea.setCaretColor(foregroundColor);
        chapterTextArea.setLineWrap(true);
        chapterTextArea.setWrapStyleWord(true);
        chapterTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chapterTextArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateChapterText(); }
            public void removeUpdate(DocumentEvent e) { updateChapterText(); }
            public void insertUpdate(DocumentEvent e) { updateChapterText(); }
        });

        JScrollPane textScrollPane = new JScrollPane(chapterTextArea);
        textScrollPane.setBackground(panelColor);
        textScrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        centerPanel.add(textScrollPane, BorderLayout.CENTER);

        // Panel droit avec les choix et les stats
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(panelColor);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        rightPanel.setPreferredSize(new Dimension(300, 0));

        // Panel des choix
        choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        choicesPanel.setBackground(panelColor);
        choicesPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor),
            "Choix",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            foregroundColor
        ));

        JScrollPane choicesScrollPane = new JScrollPane(choicesPanel);
        choicesScrollPane.setBackground(panelColor);
        choicesScrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        choicesScrollPane.setPreferredSize(new Dimension(300, 200));

        // Panel des stats
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(panelColor);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor),
            "Modificateurs de stats",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            foregroundColor
        ));

        JScrollPane statsScrollPane = new JScrollPane(statsPanel);
        statsScrollPane.setBackground(panelColor);
        statsScrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        statsScrollPane.setPreferredSize(new Dimension(300, 150));

        // Panel de l'ennemi
        enemyPanel = new JPanel();
        enemyPanel.setLayout(new BoxLayout(enemyPanel, BoxLayout.Y_AXIS));
        enemyPanel.setBackground(panelColor);
        enemyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor),
            "Configuration du combat",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            foregroundColor
        ));

        JScrollPane enemyScrollPane = new JScrollPane(enemyPanel);
        enemyScrollPane.setBackground(panelColor);
        enemyScrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        enemyScrollPane.setPreferredSize(new Dimension(300, 200));

        // Ajouter les panels au panel droit
        rightPanel.add(choicesScrollPane, BorderLayout.NORTH);
        rightPanel.add(statsScrollPane, BorderLayout.CENTER);
        rightPanel.add(enemyScrollPane, BorderLayout.SOUTH);

        // Ajouter les panels principaux
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Barre d'outils principale
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(panelColor);
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton saveButton = createStyledButton("💾 Sauvegarder");
        saveButton.setPreferredSize(new Dimension(100, 30));
        saveButton.addActionListener(e -> saveScenario());

        JButton loadButton = createStyledButton("📂 Charger");
        loadButton.setPreferredSize(new Dimension(100, 30));
        loadButton.addActionListener(e -> loadScenario());

        JButton newButton = createStyledButton("📄 Nouveau");
        newButton.setPreferredSize(new Dimension(100, 30));
        newButton.addActionListener(e -> createNewScenario());

        JButton helpButton = createStyledButton("❓ Aide");
        helpButton.setPreferredSize(new Dimension(100, 30));
        helpButton.addActionListener(e -> showHelp());

        toolBar.add(saveButton);
        toolBar.add(loadButton);
        toolBar.add(newButton);
        toolBar.add(helpButton);

        add(toolBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(accentColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 30));
        button.setEnabled(true);
        
        // Ajouter un effet de survol
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(accentColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(accentColor);
            }
        });
        
        return button;
    }

    private void loadChapters() {
        chapterListModel.clear();
        if (storyManager != null && storyManager.getCurrentScenario() != null) {
            for (Chapter chapter : storyManager.getCurrentScenario().getChapters()) {
                chapterListModel.addElement(chapter);
            }
        } else {
            // Créer un scénario par défaut si aucun n'existe
            Scenario defaultScenario = new Scenario("Nouveau scénario");
            Chapter chapter1 = new Chapter(1, "Bienvenue dans le jeu !");
            chapter1.addChoice("Commencer l'aventure", 2);
            defaultScenario.addChapter(chapter1);
            
            if (storyManager == null) {
                storyManager = new StoryManager(new Personnage("Temp", "Guerrier"));
            }
            storyManager.getCurrentScenario().addChapter(chapter1);
            chapterListModel.addElement(chapter1);
        }
    }

    private void loadChapter(Chapter chapter) {
        currentChapter = chapter;
        chapterTextArea.setText(chapter.getText());
        updateChoicesPanel();
        updateStatsPanel();
        updateEnemyPanel();
    }

    private void updateChapterText() {
        if (currentChapter != null) {
            currentChapter.setText(chapterTextArea.getText());
        }
    }

    private void updateChoicesPanel() {
        choicesPanel.removeAll();
        if (currentChapter != null) {
            for (fr.aryvoxx.projava.model.Choice choice : currentChapter.getChoices()) {
                JPanel choicePanel = new JPanel(new BorderLayout(5, 5));
                choicePanel.setBackground(panelColor);

                // Texte du choix
                JTextField textField = new JTextField(choice.getText());
                textField.setBackground(panelColor);
                textField.setForeground(foregroundColor);
                textField.setCaretColor(foregroundColor);
                textField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { choice.setText(textField.getText()); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { choice.setText(textField.getText()); }
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { choice.setText(textField.getText()); }
                });

                // Numéro du chapitre cible
                JLabel targetLabel = new JLabel("Chapitre cible:");
                targetLabel.setForeground(foregroundColor);
                JSpinner chapterSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(
                    choice.getNextChapter(), 1, 9999, 1));
                chapterSpinner.setBackground(panelColor);
                chapterSpinner.setForeground(foregroundColor);
                chapterSpinner.addChangeListener(e -> choice.setNextChapter((Integer)chapterSpinner.getValue()));

                // Bouton de suppression
                JButton deleteButton = createStyledButton("X");
                deleteButton.addActionListener(e -> {
                    currentChapter.getChoices().remove(choice);
                    updateChoicesPanel();
                });

                // Assemblage du panel
                JPanel targetPanel = new JPanel(new BorderLayout(5, 0));
                targetPanel.setBackground(panelColor);
                targetPanel.add(targetLabel, BorderLayout.WEST);
                targetPanel.add(chapterSpinner, BorderLayout.CENTER);

                choicePanel.add(textField, BorderLayout.CENTER);
                choicePanel.add(targetPanel, BorderLayout.EAST);
                choicePanel.add(deleteButton, BorderLayout.WEST);

                choicesPanel.add(choicePanel);
            }

            JButton addChoiceButton = createStyledButton("+ Ajouter un choix");
            addChoiceButton.addActionListener(e -> {
                currentChapter.addChoice("Nouveau choix", 1);
                updateChoicesPanel();
            });
            choicesPanel.add(addChoiceButton);
        }
        choicesPanel.revalidate();
        choicesPanel.repaint();
    }

    private void updateStatsPanel() {
        statsPanel.removeAll();
        if (currentChapter != null) {
            // Titre du chapitre et numéro
            JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
            titlePanel.setBackground(panelColor);
            
            JLabel chapterLabel = new JLabel("Numéro du chapitre:");
            chapterLabel.setForeground(foregroundColor);
            JSpinner chapterSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(
                currentChapter.getId(), 1, 9999, 1));
            chapterSpinner.addChangeListener(e -> {
                int newId = (Integer)chapterSpinner.getValue();
                currentChapter.setId(newId);
                chapterList.repaint();
            });
            
            titlePanel.add(chapterLabel, BorderLayout.WEST);
            titlePanel.add(chapterSpinner, BorderLayout.CENTER);
            statsPanel.add(titlePanel);

            // Séparateur
            statsPanel.add(new JSeparator());

            // Modificateurs de statistiques
            JLabel statsTitle = new JLabel("Modificateurs de statistiques:");
            statsTitle.setForeground(foregroundColor);
            statsTitle.setFont(new Font("Arial", Font.BOLD, 12));
            statsPanel.add(statsTitle);

            String[] stats = {"HABILETÉ", "ENDURANCE", "CHANCE", "PROVISIONS"};
            for (String stat : stats) {
                JPanel statPanel = new JPanel(new BorderLayout(5, 5));
                statPanel.setBackground(panelColor);

                JLabel label = new JLabel(stat);
                label.setForeground(foregroundColor);

                JSpinner spinner = new JSpinner(new javax.swing.SpinnerNumberModel(
                    Integer.valueOf(currentChapter.getStatModifiers().getOrDefault(stat, 0)),
                    Integer.valueOf(-10),
                    Integer.valueOf(10),
                    Integer.valueOf(1)));
                spinner.addChangeListener(e -> {
                    currentChapter.getStatModifiers().put(stat, (Integer)spinner.getValue());
                });

                statPanel.add(label, BorderLayout.WEST);
                statPanel.add(spinner, BorderLayout.EAST);
                statsPanel.add(statPanel);
            }
        }
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void updateEnemyPanel() {
        enemyPanel.removeAll();
        if (currentChapter != null) {
            // Titre de la section combat
            JLabel combatTitle = new JLabel("Configuration du combat:");
            combatTitle.setForeground(foregroundColor);
            combatTitle.setFont(new Font("Arial", Font.BOLD, 12));
            enemyPanel.add(combatTitle);

            // Case à cocher pour activer le combat
            JCheckBox combatCheckBox = new JCheckBox("Activer le combat");
            combatCheckBox.setBackground(panelColor);
            combatCheckBox.setForeground(foregroundColor);
            combatCheckBox.setSelected(currentChapter.requiresCombat());
            combatCheckBox.addActionListener(e -> {
                if (combatCheckBox.isSelected()) {
                    if (currentChapter.getEnemy() == null) {
                        currentChapter.setCombat(new Creature("Nouvel ennemi", 7, 10));
                    }
                } else {
                    currentChapter.setCombat(null);
                }
                updateEnemyPanel();
            });
            enemyPanel.add(combatCheckBox);

            if (currentChapter.requiresCombat() && currentChapter.getEnemy() != null) {
                Creature enemy = currentChapter.getEnemy();

                // Nom de l'ennemi
                JPanel namePanel = new JPanel(new BorderLayout(5, 5));
                namePanel.setBackground(panelColor);
                JLabel nameLabel = new JLabel("Nom de l'ennemi:");
                nameLabel.setForeground(foregroundColor);
                JTextField nameField = new JTextField(enemy.getName());
                nameField.setBackground(panelColor);
                nameField.setForeground(foregroundColor);
                nameField.setCaretColor(foregroundColor);
                nameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { enemy.setName(nameField.getText()); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { enemy.setName(nameField.getText()); }
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { enemy.setName(nameField.getText()); }
                });
                namePanel.add(nameLabel, BorderLayout.WEST);
                namePanel.add(nameField, BorderLayout.CENTER);
                enemyPanel.add(namePanel);

                // Statistiques de l'ennemi
                JPanel skillPanel = new JPanel(new BorderLayout(5, 5));
                skillPanel.setBackground(panelColor);
                JLabel skillLabel = new JLabel("Habileté:");
                skillLabel.setForeground(foregroundColor);
                JSpinner skillSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(
                    Integer.valueOf(enemy.getSkill()),
                    Integer.valueOf(1),
                    Integer.valueOf(12),
                    Integer.valueOf(1)));
                skillSpinner.setBackground(panelColor);
                skillSpinner.setForeground(foregroundColor);
                skillSpinner.addChangeListener(e -> enemy.setSkill((Integer)skillSpinner.getValue()));
                skillPanel.add(skillLabel, BorderLayout.WEST);
                skillPanel.add(skillSpinner, BorderLayout.CENTER);
                enemyPanel.add(skillPanel);

                JPanel staminaPanel = new JPanel(new BorderLayout(5, 5));
                staminaPanel.setBackground(panelColor);
                JLabel staminaLabel = new JLabel("Endurance:");
                staminaLabel.setForeground(foregroundColor);
                JSpinner staminaSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(
                    Integer.valueOf(enemy.getStamina()),
                    Integer.valueOf(1),
                    Integer.valueOf(20),
                    Integer.valueOf(1)));
                staminaSpinner.setBackground(panelColor);
                staminaSpinner.setForeground(foregroundColor);
                staminaSpinner.addChangeListener(e -> enemy.setStamina((Integer)staminaSpinner.getValue()));
                staminaPanel.add(staminaLabel, BorderLayout.WEST);
                staminaPanel.add(staminaSpinner, BorderLayout.CENTER);
                enemyPanel.add(staminaPanel);
            }
        }
        enemyPanel.revalidate();
        enemyPanel.repaint();
    }

    private void filterChapters(String searchText) {
        chapterListModel.clear();
        if (storyManager != null && storyManager.getCurrentScenario() != null) {
            for (Chapter chapter : storyManager.getCurrentScenario().getChapters()) {
                if (searchText.isEmpty() || 
                    chapter.getText().toLowerCase().contains(searchText.toLowerCase()) ||
                    String.valueOf(chapter.getId()).contains(searchText)) {
                    chapterListModel.addElement(chapter);
                }
            }
        }
    }

    private void createNewChapter() {
        if (storyManager != null && storyManager.getCurrentScenario() != null) {
            int newId = storyManager.getCurrentScenario().getChapters().size() + 1;
            Chapter newChapter = new Chapter(newId, "Nouveau chapitre");
            storyManager.getCurrentScenario().addChapter(newChapter);
            chapterListModel.addElement(newChapter);
            chapterList.setSelectedValue(newChapter, true);
        }
    }

    private void deleteSelectedChapter() {
        Chapter selected = chapterList.getSelectedValue();
        if (selected != null) {
            int response = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer ce chapitre ?\nCette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION);
                
            if (response == JOptionPane.YES_OPTION) {
                storyManager.getCurrentScenario().removeChapter(selected);
                chapterListModel.removeElement(selected);
                currentChapter = null;
                chapterTextArea.setText("");
                updateChoicesPanel();
                updateStatsPanel();
                updateEnemyPanel();
            }
        }
    }

    private void saveScenario() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sauvegarder le scénario");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers texte", "txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            
            try {
                storyManager.getCurrentScenario().saveToFile(file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, 
                    "Scénario sauvegardé avec succès dans :\n" + file.getAbsolutePath(),
                    "Sauvegarde réussie",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la sauvegarde : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showHelp() {
        String helpText = """
            Guide de l'éditeur de scénario:
            
            1. Création de chapitres:
               - Cliquez sur "+ Nouveau" pour créer un chapitre
               - Double-cliquez sur un chapitre pour l'éditer
               
            2. Édition du contenu:
               - Utilisez la zone de texte centrale pour écrire le contenu
               - Ajoutez des choix avec le bouton "+ Ajouter un choix"
               - Configurez les statistiques dans le panneau de droite
               
            3. Combat:
               - Activez le combat dans l'onglet "Combat"
               - Définissez les statistiques de l'ennemi
               
            4. Sauvegarde:
               - Utilisez Ctrl+S ou le bouton 💾 pour sauvegarder
               - Les fichiers sont sauvegardés au format .txt
               
            5. Raccourcis clavier:
               - Ctrl+S : Sauvegarder
               - Ctrl+O : Charger
               - Ctrl+N : Nouveau scénario
            """;
            
        JTextArea helpArea = new JTextArea(helpText);
        helpArea.setEditable(false);
        helpArea.setBackground(panelColor);
        helpArea.setForeground(foregroundColor);
        helpArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(helpArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Aide de l'éditeur", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void createNewScenario() {
        int response = JOptionPane.showConfirmDialog(this,
            "Voulez-vous créer un nouveau scénario ?\nToutes les modifications non sauvegardées seront perdues.",
            "Nouveau scénario",
            JOptionPane.YES_NO_OPTION);
            
        if (response == JOptionPane.YES_OPTION) {
            Scenario newScenario = new Scenario("Nouveau scénario");
            Chapter chapter1 = new Chapter(1, "Bienvenue dans le jeu !");
            chapter1.addChoice("Commencer l'aventure", 2);
            newScenario.addChapter(chapter1);
            
            storyManager = new StoryManager(new Personnage("Temp", "Guerrier"));
            storyManager.setCurrentScenario(newScenario);
            
            loadChapters();
            chapterList.setSelectedIndex(0);
        }
    }

    private void loadScenario() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Charger un scénario");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers texte", "txt"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Scenario loadedScenario = Scenario.loadFromFile(file.getAbsolutePath());
                storyManager.setCurrentScenario(loadedScenario);
                loadChapters();
                chapterList.setSelectedIndex(0);
                
                JOptionPane.showMessageDialog(this,
                    "Scénario chargé avec succès !",
                    "Chargement réussi",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 