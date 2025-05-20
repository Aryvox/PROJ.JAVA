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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getInitialStamina() {
        return initialStamina;
    }

    public ImageIcon getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageIcon avatar) {
        this.avatar = avatar;
    }

    public boolean isAlive() {
        return stamina > 0;
    }

    public void takeDamage(int damage) {
        stamina = Math.max(0, stamina - damage);
    }

    public int getAttackStrength() {
        return skill + (int)(Math.random() * 6) + 1;
    }

    // Getters
    public String getAvatarPath() { return avatarPath; }
} 