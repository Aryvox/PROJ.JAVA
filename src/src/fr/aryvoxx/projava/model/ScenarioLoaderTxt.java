package fr.aryvoxx.projava.model;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScenarioLoaderTxt {

    private static final Pattern STAT_MODIFIER_PATTERN = Pattern.compile("\\[MOD_STATS:([a-zA-Z]+):(-?\\d+)\\]");

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
                    // End of a chapter block
                    if (current != null) {
                        current.setText(textBuilder.toString().trim());
                        for (String[] choice : pendingChoices) {
                            current.addChoice(choice[0], Integer.parseInt(choice[1]));
                        }
                        chapters.add(current);
                        current = null;
                        textBuilder.setLength(0);
                        pendingChoices.clear();
                    }
                    continue; // Skip empty lines
                }

                if (line.startsWith("#")) {
                    // Start of a new chapter
                    if (current != null) {
                        // Save previous chapter if not empty
                        current.setText(textBuilder.toString().trim());
                        for (String[] choice : pendingChoices) {
                            current.addChoice(choice[0], Integer.parseInt(choice[1]));
                        }
                        chapters.add(current);
                    }
                    int id = Integer.parseInt(line.substring(1));
                    current = new Chapter(id, ""); // New chapter started, reset text and choices
                    textBuilder.setLength(0);
                    pendingChoices.clear();
                } else if (current != null) {
                    // Inside a chapter block

                    // Check for and process stat modifiers within the line
                    Matcher matcher = STAT_MODIFIER_PATTERN.matcher(line);
                    System.out.println("Processing line: " + line); // Debug log
                    while (matcher.find()) {
                        String statName = matcher.group(1).toLowerCase(); // Convert stat name to lowercase
                        try {
                            int value = Integer.parseInt(matcher.group(2));
                            System.out.println("Found stat modifier - Stat: " + statName + ", Value: " + value); // Debug log
                            current.addStatModifier(statName, value);
                            System.out.println("Added stat modifier: " + statName + " : " + value + " for chapter " + current.getId());
                        } catch (NumberFormatException e) {
                            System.err.println("Erreur de format pour la valeur de statistique: " + line);
                        }

                        // Remove the matched tag from the line
                        String originalLine = line;
                        line = line.replace(matcher.group(0), "").trim();
                        System.out.println("Line after removing modifier: " + line); // Debug log
                        // Reset matcher to find other tags in the modified line
                        matcher = STAT_MODIFIER_PATTERN.matcher(line);
                    }

                    // Check for choice (texte|idSuivant). Exclude MAX_ASSAUTS which is not a simple choice
                    if (line.contains("|") && !line.startsWith("MAX_ASSAUTS:")) {
                         String[] parts = line.split("\\|");
                         if (parts.length == 2) {
                             pendingChoices.add(new String[]{parts[0].trim(), parts[1].trim()});
                         } else {
                             System.err.println("Format de choix invalide: " + line);
                             // If format is invalid, treat as regular text line
                             textBuilder.append(line).append("\n");
                         }
                         // If it is a valid choice, do NOT add this line to the chapter text
                         if(parts.length == 2) continue; // Move to the next line
                    } else if (!line.isEmpty()) { // Only add non-empty lines after tag removal
                        // Regular text line - add to text builder
                        textBuilder.append(line).append("\n");
                    }
                }
            }

            // Add the last chapter if not empty
            if (current != null) {
                current.setText(textBuilder.toString().trim());
                for (String[] choice : pendingChoices) {
                    current.addChoice(choice[0], Integer.parseInt(choice[1]));
                }
                chapters.add(current);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return chapters;
    }
}