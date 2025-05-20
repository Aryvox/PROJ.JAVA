package fr.aryvoxx.projava.model;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class StoryManager {
    private Scenario currentScenario;
    private Chapter currentChapter;
    private Personnage personnage;

    public StoryManager(Personnage personnage) {
        this.personnage = personnage;
        try {
            this.currentScenario = Scenario.loadFromFile("scenario.txt");
            this.currentChapter = currentScenario.getChapter(1);
        } catch (IOException e) {
            e.printStackTrace();
            // Créer un scénario par défaut si le fichier n'existe pas
            this.currentScenario = new Scenario("Nouveau scénario");
            Chapter chapter1 = new Chapter(1, "Bienvenue dans le jeu !");
            chapter1.addChoice("Commencer l'aventure", 2);
            currentScenario.addChapter(chapter1);
            this.currentChapter = chapter1;
        }
    }

    public Chapter getCurrentChapter() {
        return currentChapter;
    }

    public void goToChapter(int chapterId) {
        Chapter nextChapter = currentScenario.getChapter(chapterId);
        if (nextChapter != null) {
            currentChapter = nextChapter;
            applyStatModifiers();
        }
    }

    public void setCurrentScenario(Scenario scenario) {
        this.currentScenario = scenario;
        this.currentChapter = scenario.getChapter(1);
    }

    private void applyStatModifiers() {
        if (currentChapter != null) {
            for (Map.Entry<String, Integer> entry : currentChapter.getStatModifiers().entrySet()) {
                String stat = entry.getKey();
                int value = entry.getValue();
                switch (stat) {
                    case "HABILETÉ":
                        personnage.setHabilete(personnage.getHabilete() + value);
                        break;
                    case "ENDURANCE":
                        personnage.setEndurance(personnage.getEndurance() + value);
                        break;
                    case "CHANCE":
                        personnage.setChance(personnage.getChance() + value);
                        break;
                    case "PROVISIONS":
                        personnage.setProvisions(personnage.getProvisions() + value);
                        break;
                }
            }
        }
    }

    public String processCombatTurn(Creature enemy, boolean isPlayerTurn, String action) {
        StringBuilder result = new StringBuilder();
        
        if (isPlayerTurn) {
            int playerAttack = personnage.getHabilete() + (int)(Math.random() * 6) + 1;
            int enemyAttack = enemy.getAttackStrength();
            
            result.append("Votre attaque : ").append(playerAttack).append("\n");
            result.append("Attaque de l'ennemi : ").append(enemyAttack).append("\n");
            
            if (playerAttack > enemyAttack) {
                int damage = playerAttack - enemyAttack;
                enemy.takeDamage(damage);
                result.append("Vous infligez ").append(damage).append(" points de dégâts !\n");
            } else if (enemyAttack > playerAttack) {
                int damage = enemyAttack - playerAttack;
                if (personnage.getEndurance() > 0) {
                    personnage.setEndurance(personnage.getEndurance() - damage);
                    result.append("L'ennemi vous inflige ").append(damage).append(" points de dégâts à votre endurance !\n");
                } else {
                    // Si l'endurance est à 0, les dégâts sont appliqués à la santé
                    int healthDamage = damage / 2; // Les dégâts sont réduits de moitié
                    personnage.setSante(personnage.getSante() - healthDamage);
                    result.append("Votre endurance est épuisée ! L'ennemi vous inflige ").append(healthDamage).append(" points de dégâts à votre santé !\n");
                }
            } else {
                result.append("Les attaques s'annulent !\n");
            }
        } else {
            int enemyAttack = enemy.getAttackStrength();
            int playerAttack = personnage.getHabilete() + (int)(Math.random() * 6) + 1;
            
            result.append("Attaque de l'ennemi : ").append(enemyAttack).append("\n");
            result.append("Votre attaque : ").append(playerAttack).append("\n");
            
            if (enemyAttack > playerAttack) {
                int damage = enemyAttack - playerAttack;
                if (personnage.getEndurance() > 0) {
                    personnage.setEndurance(personnage.getEndurance() - damage);
                    result.append("L'ennemi vous inflige ").append(damage).append(" points de dégâts à votre endurance !\n");
                } else {
                    // Si l'endurance est à 0, les dégâts sont appliqués à la santé
                    int healthDamage = damage / 2; // Les dégâts sont réduits de moitié
                    personnage.setSante(personnage.getSante() - healthDamage);
                    result.append("Votre endurance est épuisée ! L'ennemi vous inflige ").append(healthDamage).append(" points de dégâts à votre santé !\n");
                }
            } else if (playerAttack > enemyAttack) {
                int damage = playerAttack - enemyAttack;
                enemy.takeDamage(damage);
                result.append("Vous infligez ").append(damage).append(" points de dégâts !\n");
            } else {
                result.append("Les attaques s'annulent !\n");
            }
        }
        
        return result.toString();
    }

    public boolean isCombatOver(Creature enemy) {
        // Le combat se termine uniquement si l'ennemi est mort ou si la santé du personnage est à 0
        return !enemy.isAlive() || personnage.getSante() <= 0;
    }

    public Scenario getCurrentScenario() {
        return currentScenario;
    }

    public void setPersonnage(Personnage personnage) {
        this.personnage = personnage;
    }
} 