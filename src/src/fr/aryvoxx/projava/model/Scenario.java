package fr.aryvoxx.projava.model;

import java.io.*;
import java.util.*;

public class Scenario {
    private List<Chapter> chapters;
    private String name;

    public Scenario(String name) {
        this.name = name;
        this.chapters = new ArrayList<>();
    }

    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
    }

    public void removeChapter(Chapter chapter) {
        chapters.remove(chapter);
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public Chapter getChapter(int id) {
        for (Chapter chapter : chapters) {
            if (chapter.getId() == id) {
                return chapter;
            }
        }
        return null;
    }

    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Chapter chapter : chapters) {
                writer.println("#" + chapter.getId());
                writer.println(chapter.getText());
                
                // Sauvegarder les choix
                for (Choice choice : chapter.getChoices()) {
                    writer.println(choice.getText() + "|" + choice.getNextChapter());
                }
                
                // Sauvegarder les modificateurs de statistiques
                for (Map.Entry<String, Integer> entry : chapter.getStatModifiers().entrySet()) {
                    writer.println("[MOD_STATS:" + entry.getKey() + ":" + entry.getValue() + "]");
                }
                
                // Sauvegarder les informations de combat
                if (chapter.requiresCombat() && chapter.getEnemy() != null) {
                    Creature enemy = chapter.getEnemy();
                    writer.println("[COMBAT:" + enemy.getName() + ":" + enemy.getSkill() + ":" + enemy.getStamina() + "]");
                }
                
                writer.println();
            }
        }
    }

    public static Scenario loadFromFile(String filename) throws IOException {
        Scenario scenario = new Scenario("Scénario chargé");
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            Chapter currentChapter = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                if (line.startsWith("#")) {
                    // Nouveau chapitre
                    int chapterId = Integer.parseInt(line.substring(1));
                    StringBuilder chapterText = new StringBuilder();
                    
                    // Lire le texte du chapitre jusqu'à trouver un choix ou un modificateur
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.contains("|") || line.startsWith("[") || line.startsWith("#")) {
                            break;
                        }
                        chapterText.append(line).append("\n");
                    }
                    
                    currentChapter = new Chapter(chapterId, chapterText.toString().trim());
                    scenario.addChapter(currentChapter);
                }
                
                if (currentChapter != null) {
                    if (line.contains("|")) {
                        // C'est un choix
                        String[] parts = line.split("\\|");
                        if (parts.length == 2) {
                            currentChapter.addChoice(parts[0], Integer.parseInt(parts[1]));
                        }
                    } else if (line.startsWith("[MOD_STATS:")) {
                        // C'est un modificateur de statistiques
                        String[] parts = line.substring(1, line.length() - 1).split(":");
                        if (parts.length == 3) {
                            currentChapter.addStatModifier(parts[1], Integer.parseInt(parts[2]));
                        }
                    } else if (line.startsWith("[COMBAT:")) {
                        // C'est une configuration de combat
                        String[] parts = line.substring(1, line.length() - 1).split(":");
                        if (parts.length == 4) {
                            Creature enemy = new Creature(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                            currentChapter.setCombat(enemy);
                        }
                    }
                }
            }
        }
        return scenario;
    }
} 