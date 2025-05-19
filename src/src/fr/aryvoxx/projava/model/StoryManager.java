package fr.aryvoxx.projava.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class StoryManager {
    private Map<Integer, Chapter> chapters;
    private int currentChapter;
    private Personnage player;
    private Scenario currentScenario;

    public StoryManager(Personnage player) {
        this.player = player;
        this.chapters = new HashMap<>();
        this.currentChapter = 1;
        // Charger le scénario depuis un fichier TXT simple
        loadScenarioFromTxt("scenario.txt");
    }

    private void loadScenarioFromTxt(String filePath) {
        List<Chapter> loadedChapters = ScenarioLoaderTxt.loadChaptersFromTxt(filePath);
        if (!loadedChapters.isEmpty()) {
            for (Chapter c : loadedChapters) {
                chapters.put(c.getId(), c);
            }
            this.currentScenario = new Scenario("Scénario personnalisé", "Scénario chargé depuis un fichier TXT", loadedChapters.get(0).getId());
            this.currentChapter = loadedChapters.get(0).getId();
        } else {
            JOptionPane.showMessageDialog(null, "Erreur : Impossible de charger le scénario depuis le fichier " + filePath);
            System.exit(1);
        }
    }

    public Chapter getCurrentChapter() {
        return chapters.get(currentChapter);
    }

    public void goToChapter(int chapterId) {
        if (chapters.containsKey(chapterId)) {
            currentChapter = chapterId;
        }
    }

    public boolean testLuck() {
        int luckRoll = (int)(Math.random() * 6 + 1) + (int)(Math.random() * 6 + 1);
        return luckRoll <= player.getIntelligence();
    }

    public boolean resolveCombat(Creature enemy) {
        // Le combat est maintenant géré par l'interface
        return true;
    }

    public String processCombatTurn(Creature enemy, boolean isPlayerTurn, String action) {
        StringBuilder result = new StringBuilder();
        
        if (isPlayerTurn) {
            switch (action) {
                case "attack":
                    int playerAttack = (int)(Math.random() * 6 + 1) + (int)(Math.random() * 6 + 1) + player.getForceTotale();
                    int enemyAttack = enemy.getAttackStrength();

                    if (playerAttack > enemyAttack) {
                        // Dégâts aléatoires entre 1 et 4
                        int damage = (int)(Math.random() * 4) + 1;
                        enemy.damage(damage);
                        result.append("Vous infligez ").append(damage).append(" points de dégâts à ").append(enemy.getName()).append(" !\n");
                    } else if (enemyAttack > playerAttack) {
                        result.append("Votre attaque est bloquée par ").append(enemy.getName()).append(" !\n");
                    } else {
                        result.append("Votre attaque est parée par ").append(enemy.getName()).append(" !\n");
                    }
                    break;

                case "defend":
                    player.setDefenseBonus(2);
                    result.append("Vous prenez une position défensive. Votre défense est augmentée !\n");
                    break;

                case "item":
                    result.append("Choisissez un objet à utiliser dans votre inventaire.\n");
                    break;
            }
        } else {
            int enemyAttack = enemy.getAttackStrength();
            int playerDefense = (int)(Math.random() * 6 + 1) + (int)(Math.random() * 6 + 1) + player.getDefenseTotale();

            if (enemyAttack > playerDefense) {
                // Dégâts aléatoires entre 1 et 3
                int damage = (int)(Math.random() * 3) + 1;
                player.prendreDegats(damage);
                result.append(enemy.getName()).append(" vous inflige ").append(damage).append(" points de dégâts !\n");
            } else if (playerDefense > enemyAttack) {
                result.append("Vous parez l'attaque de ").append(enemy.getName()).append(" !\n");
            } else {
                result.append("L'attaque de ").append(enemy.getName()).append(" est bloquée !\n");
            }
        }

        // Vérifier si le combat est terminé
        if (!enemy.isAlive()) {
            result.append("\nVous avez vaincu ").append(enemy.getName()).append(" !");
            player.gagnerExperience(20);
            // Ajouter une potion de vie comme récompense
            player.ajouterItem(new Item("Potion de vie", "Restaure 20 points de vie", Item.ItemType.POTION_VIE, 20));
        } else if (player.getPointsDeVie() <= 0) {
            result.append("\nVous avez été vaincu par ").append(enemy.getName()).append(" !");
        }

        return result.toString();
    }

    public boolean isCombatOver(Creature enemy) {
        return !enemy.isAlive() || player.getPointsDeVie() <= 0;
    }

    public Scenario getCurrentScenario() {
        return currentScenario;
    }
} 