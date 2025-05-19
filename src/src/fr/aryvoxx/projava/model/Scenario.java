package fr.aryvoxx.projava.model;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
    private String title;
    private String description;
    private List<Chapter> chapters;
    private int startChapterId;

    public Scenario(String title, String description, int startChapterId) {
        this.title = title;
        this.description = description;
        this.chapters = new ArrayList<>();
        this.startChapterId = startChapterId;
    }

    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
    }

    public Chapter getChapter(int id) {
        return chapters.stream()
                .filter(chapter -> chapter.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public int getStartChapterId() {
        return startChapterId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
} 