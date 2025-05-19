package fr.aryvoxx.projava.model;

import java.io.*;
import java.util.*;

public class ScenarioLoaderTxt {
    public static List<Chapter> loadChaptersFromTxt(String filePath) {
        List<Chapter> chapters = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            Chapter current = null;
            StringBuilder textBuilder = new StringBuilder();
            List<String[]> pendingChoices = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    if (current != null) {
                        // Ajouter le texte accumulé
                        current = new Chapter(current.getId(), textBuilder.toString().trim());
                        // Ajouter les choix en attente
                        for (String[] choice : pendingChoices) {
                            current.addChoice(choice[0], Integer.parseInt(choice[1]));
                        }
                        chapters.add(current);
                        current = null;
                        textBuilder.setLength(0);
                        pendingChoices.clear();
                    }
                    continue;
                }
                if (line.startsWith("#")) {
                    int id = Integer.parseInt(line.substring(1));
                    current = new Chapter(id, "");
                    textBuilder.setLength(0);
                    pendingChoices.clear();
                } else if (current != null) {
                    // Si c'est un choix (texte|idSuivant)
                    if (line.contains("|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length == 2) {
                            pendingChoices.add(new String[]{parts[0], parts[1]});
                        } else {
                            // Si c'est une config de combat ou autre, on ajoute la ligne au texte
                            textBuilder.append(line).append("\n");
                        }
                    } else {
                        textBuilder.append(line).append("\n");
                    }
                }
            }
            if (current != null) {
                current = new Chapter(current.getId(), textBuilder.toString().trim());
                for (String[] choice : pendingChoices) {
                    current.addChoice(choice[0], Integer.parseInt(choice[1]));
                }
                chapters.add(current);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chapters;
    }
} 