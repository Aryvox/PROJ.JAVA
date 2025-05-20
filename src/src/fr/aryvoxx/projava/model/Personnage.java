package fr.aryvoxx.projava.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import fr.aryvoxx.projava.view.GameFrame;
import fr.aryvoxx.projava.model.Item;
import javax.swing.JOptionPane;

public class Personnage implements Serializable {
    private String nom;
    private int pointsDeVie;
    private int sante;
    private int santeInitiale;
    private int habilete;
    private int endurance;
    private int chance;
    private int habileteInitiale;
    private int enduranceInitiale;
    private int chanceInitiale;
    private int provisions;
    private String imagePath;
    private int niveau;
    private int experience;
    private List<Item> inventaire;
    private int forceBonus;
    private int defenseBonus;
    private int pointsDisponibles;
    private GameFrame gameFrame;

    public Personnage(String nom, String avatarType) {
        this.nom = nom;
        this.pointsDisponibles = 10;
        
        // Initialisation des attributs selon le type d'avatar
        switch (avatarType.toLowerCase()) {
            case "guerrier":
                this.habileteInitiale = 8;
                this.enduranceInitiale = 18;
                this.chanceInitiale = 8;
                this.santeInitiale = 120;
                break;
            case "mage":
                this.habileteInitiale = 6;
                this.enduranceInitiale = 14;
                this.chanceInitiale = 12;
                this.santeInitiale = 90;
                break;
            case "archer":
                this.habileteInitiale = 12;
                this.enduranceInitiale = 16;
                this.chanceInitiale = 10;
                this.santeInitiale = 100;
                break;
            default: // Type par défaut
                this.habileteInitiale = 10;
                this.enduranceInitiale = 20;
                this.chanceInitiale = 10;
                this.santeInitiale = 100;
        }

        this.habilete = this.habileteInitiale;
        this.endurance = this.enduranceInitiale;
        this.chance = this.chanceInitiale;
        this.sante = this.santeInitiale;
        this.pointsDeVie = this.sante;
        this.provisions = 10;
        this.niveau = 1;
        this.experience = 0;
        this.inventaire = new ArrayList<>();
        this.forceBonus = 0;
        this.defenseBonus = 0;
    }

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public GameFrame getGameFrame() {
        return this.gameFrame;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getPointsDeVie() {
        return pointsDeVie;
    }

    public void setPointsDeVie(int pointsDeVie) {
        this.pointsDeVie = pointsDeVie;
    }

    public int getSante() {
        return sante;
    }

    public void setSante(int sante) {
        this.sante = Math.min(sante, santeInitiale);
        this.pointsDeVie = this.sante;
        if (gameFrame != null) {
            gameFrame.updateStats();
        }
    }

    public int getSanteInitiale() {
        return santeInitiale;
    }

    public int getHabilete() {
        return habilete;
    }

    public void setHabilete(int habilete) {
        this.habilete = Math.min(habilete, habileteInitiale);
        if (gameFrame != null) {
            gameFrame.updateStats();
        }
    }

    public int getEndurance() {
        return endurance;
    }

    public void setEndurance(int endurance) {
        this.endurance = Math.max(0, Math.min(endurance, enduranceInitiale));
        System.out.println("Endurance set to: " + this.endurance); // Debug log
        if (gameFrame != null) {
            gameFrame.updateStats();
        }
        
        // Check if character is dead
        if (this.endurance <= 0) {
            handleDeath();
        }
    }

    private void handleDeath() {
        System.out.println("Le personnage est mort !");
        if (gameFrame != null) {
            // Afficher un message de mort
            JOptionPane.showMessageDialog(gameFrame, 
                "Votre personnage est mort !\nL'aventure se termine ici...", 
                "Mort du personnage", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Retourner au menu principal
            gameFrame.showMainMenu();
        }
    }

    public boolean isDead() {
        return this.sante <= 0;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = Math.min(chance, chanceInitiale);
        if (gameFrame != null) {
            gameFrame.updateStats();
        }
    }

    public int getProvisions() {
        return provisions;
    }

    public void setProvisions(int provisions) {
        this.provisions = provisions;
        if (gameFrame != null) {
            gameFrame.updateStats();
        }
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public List<Item> getInventaire() {
        return inventaire;
    }

    public void ajouterItem(Item item) {
        inventaire.add(item);
    }

    public void utiliserItem(Item item) {
        switch (item.getType()) {
            case POTION_VIE:
                soigner(item.getValue());
                break;
            case POTION_FORCE:
                forceBonus = item.getValue();
                break;
            case ARMURE:
                defenseBonus = item.getValue();
                break;
        }
        inventaire.remove(item);
    }

    public int getForceTotale() {
        return habilete + forceBonus;
    }

    public int getDefenseTotale() {
        return endurance + defenseBonus;
    }

    public void setDefenseBonus(int bonus) {
        this.defenseBonus = bonus;
    }

    // Méthodes pour gérer les points de vie
    public void prendreDegats(int degats) {
        int degatsReduits = Math.max(1, degats - defenseBonus);
        // Réduire l'endurance
        this.endurance = Math.max(0, this.endurance - degatsReduits);
        // Réduire la santé proportionnellement
        int reductionSante = (degatsReduits * this.santeInitiale) / this.enduranceInitiale;
        this.sante = Math.max(0, this.sante - reductionSante);
        this.pointsDeVie = this.sante;
        
        // Check if character is dead after taking damage
        if (this.endurance <= 0 || this.sante <= 0) {
            handleDeath();
        }
    }

    public void soigner(int pointsDeSoin) {
        // Soigner l'endurance
        this.endurance = Math.min(enduranceInitiale, this.endurance + pointsDeSoin);
        // Soigner la santé proportionnellement
        int soinSante = (pointsDeSoin * this.santeInitiale) / this.enduranceInitiale;
        this.sante = Math.min(santeInitiale, this.sante + soinSante);
        this.pointsDeVie = this.sante;
    }

    // Méthode pour gagner de l'expérience
    public void gagnerExperience(int exp) {
        this.experience += exp;
        // Logique de montée de niveau
        if (this.experience >= niveau * 100) {
            monterNiveau();
        }
    }

    private void monterNiveau() {
        this.niveau++;
        this.pointsDeVie += 20;
        this.habilete += 2;
        this.endurance += 2;
        this.chance += 2;
    }

    // Nouveaux getters et setters pour les attributs de combat
    public int getForceAttaque() {
        return (int)(Math.random() * 6 + 1) + (int)(Math.random() * 6 + 1) + habilete;
    }

    public void utiliserProvision() {
        if (provisions > 0) {
            soigner(4);
            provisions--;
        }
    }

    public boolean tenterChance() {
        if (chance > 0) {
            int roll = (int)(Math.random() * 6 + 1) + (int)(Math.random() * 6 + 1);
            chance--;
            return roll <= chance;
        }
        return false;
    }

    public void restaurerHabilete() {
        this.habilete = habileteInitiale;
    }

    public void restaurerEndurance() {
        this.endurance = enduranceInitiale;
    }

    public void restaurerChance() {
        this.chance = chanceInitiale;
    }

    public void augmenterChanceInitiale() {
        this.chanceInitiale++;
        this.chance = chanceInitiale;
    }

    public int getHabileteInitiale() {
        return habileteInitiale;
    }

    public int getEnduranceInitiale() {
        return enduranceInitiale;
    }

    public int getChanceInitiale() {
        return chanceInitiale;
    }

    public int getPointsDisponibles() {
        return pointsDisponibles;
    }

    public void setPointsDisponibles(int points) {
        this.pointsDisponibles = points;
    }

    public boolean augmenterHabilete() {
        if (pointsDisponibles > 0) {
            habilete++;
            habileteInitiale++;
            pointsDisponibles--;
            System.out.println("Habilete augmentée: " + habilete + ", Points disponibles: " + pointsDisponibles);
            return true;
        }
        return false;
    }

    public boolean augmenterEndurance() {
        if (pointsDisponibles > 0) {
            endurance++;
            enduranceInitiale++;
            pointsDisponibles--;
            System.out.println("Endurance augmentée: " + endurance + ", Points disponibles: " + pointsDisponibles);
            return true;
        }
        return false;
    }

    public boolean augmenterChance() {
        if (pointsDisponibles > 0) {
            chance++;
            chanceInitiale++;
            pointsDisponibles--;
            System.out.println("Chance augmentée: " + chance + ", Points disponibles: " + pointsDisponibles);
            return true;
        }
        return false;
    }

    public boolean augmenterSante() {
        if (pointsDisponibles > 0) {
            sante += 10;
            santeInitiale += 10;
            pointsDeVie = sante;
            pointsDisponibles--;
            System.out.println("Santé augmentée: " + sante + ", Points disponibles: " + pointsDisponibles);
            return true;
        }
        return false;
    }
} 