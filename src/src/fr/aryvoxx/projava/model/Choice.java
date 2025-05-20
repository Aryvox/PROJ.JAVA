package fr.aryvoxx.projava.model;

public class Choice {
    private String text;
    private int nextChapter;

    public Choice(String text, int nextChapter) {
        this.text = text;
        this.nextChapter = nextChapter;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNextChapter() {
        return nextChapter;
    }

    public void setNextChapter(int nextChapter) {
        this.nextChapter = nextChapter;
    }
} 