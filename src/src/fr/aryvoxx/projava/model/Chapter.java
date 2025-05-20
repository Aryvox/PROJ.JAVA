package fr.aryvoxx.projava.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chapter {
    private int id;
    private String text;
    private List<Choice> choices;
    private boolean requiresCombat;
    private Creature enemy;
    private boolean requiresLuckTest;
    private int luckTestSuccessChapter;
    private int luckTestFailureChapter;
    private Map<String, Integer> statModifiers;

    public Chapter(int id, String text) {
        this.id = id;
        this.text = text;
        this.choices = new ArrayList<>();
        this.requiresCombat = false;
        this.requiresLuckTest = false;
        this.statModifiers = new HashMap<>();
    }

    public void addChoice(String text, int nextChapter) {
        choices.add(new Choice(text, nextChapter));
    }

    public void setCombat(Creature enemy) {
        this.requiresCombat = true;
        this.enemy = enemy;
    }

    public void setLuckTest(int successChapter, int failureChapter) {
        this.requiresLuckTest = true;
        this.luckTestSuccessChapter = successChapter;
        this.luckTestFailureChapter = failureChapter;
    }

    public void addStatModifier(String stat, int value) {
        this.statModifiers.put(stat, value);
    }

    // Getters
    public int getId() { return id; }
    public String getText() { return text; }
    public List<Choice> getChoices() { return choices; }
    public boolean requiresCombat() { return requiresCombat; }
    public Creature getEnemy() { return enemy; }
    public boolean requiresLuckTest() { return requiresLuckTest; }
    public int getLuckTestSuccessChapter() { return luckTestSuccessChapter; }
    public int getLuckTestFailureChapter() { return luckTestFailureChapter; }
    public Map<String, Integer> getStatModifiers() { return statModifiers; }

    // Setters
    public void setText(String text) {
        this.text = text;
    }

    public void setId(int id) {
        this.id = id;
    }
} 