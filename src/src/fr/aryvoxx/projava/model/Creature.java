package fr.aryvoxx.projava.model;

import javax.swing.ImageIcon;

public class Creature {
    private String name;
    private int skill;
    private int stamina;
    private int initialStamina;
    private ImageIcon avatar;
    private String avatarPath;

    public Creature(String name, int skill, int stamina) {
        this.name = name;
        this.skill = skill;
        this.stamina = stamina;
        this.initialStamina = stamina;
        this.avatarPath = "src/resources/enemies/" + name.toLowerCase().replace(" ", "_") + ".png";
        try {
            this.avatar = new ImageIcon(avatarPath);
        } catch (Exception e) {
            // Si l'image n'existe pas, on utilise une image par défaut
            this.avatar = new ImageIcon("src/resources/enemies/default_enemy.png");
        }
    }

    public int getAttackStrength() {
        return (int)(Math.random() * 6 + 1) + (int)(Math.random() * 6 + 1) + skill;
    }

    public void damage(int amount) {
        stamina = Math.max(0, stamina - amount);
    }

    public boolean isAlive() {
        return stamina > 0;
    }

    // Getters
    public String getName() { return name; }
    public int getSkill() { return skill; }
    public int getStamina() { return stamina; }
    public int getInitialStamina() { return initialStamina; }
    public ImageIcon getAvatar() { return avatar; }
    public String getAvatarPath() { return avatarPath; }
} 