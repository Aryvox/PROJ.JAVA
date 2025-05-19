package fr.aryvoxx.projava.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Personnage implements Serializable {
    private String nom;
    private int pointsDeVie;
    private int force;
    private int agilite;
    private int intelligence;
    private String imagePath;
    private int niveau;
    private int experience;
    private List<Item> inventaire;
    private int forceBonus;
    private int defenseBonus;

    public Personnage(String nom) {
        this.nom = nom;
        this.pointsDeVie = 100;
        this.force = 10;
        this.agilite = 10;
        this.intelligence = 10;
        this.niveau = 1;
        this.experience = 0;
        this.inventaire = new ArrayList<>();
        this.forceBonus = 0;
        this.defenseBonus = 0;
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

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public int getAgilite() {
        return agilite;
    }

    public void setAgilite(int agilite) {
        this.agilite = agilite;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
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
        return force + forceBonus;
    }

    public int getDefenseTotale() {
        return agilite + defenseBonus;
    }

    public void setDefenseBonus(int bonus) {
        this.defenseBonus = bonus;
    }

    // Méthodes pour gérer les points de vie
    public void prendreDegats(int degats) {
        int degatsReduits = Math.max(1, degats - defenseBonus);
        this.pointsDeVie = Math.max(0, this.pointsDeVie - degatsReduits);
    }

    public void soigner(int pointsDeSoin) {
        this.pointsDeVie = Math.min(100, this.pointsDeVie + pointsDeSoin);
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
        this.force += 2;
        this.agilite += 2;
        this.intelligence += 2;
    }
} 