package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by podczemd on 4/16/2018.
 */

public class Hunt {
    private String title;
    private String creator;
    private String description;
    private ArrayList<Hint> hints;
    private String finalMessage;
    private int currentHint = 0;

    public Hunt(String creator){
        this.creator=creator;
        this.hints = new ArrayList<>();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Hint> getHints() {
        return hints;
    }

    public void addHint(Hint hint) {
        this.hints.add(hint);
    }

    public void setHint(int index, Hint hint){
        this.hints.set(index,hint);
    }

    public String getFinalMessage() {
        return finalMessage;
    }

    public void setFinalMessage(String finalMessage) {
        this.finalMessage = finalMessage;
    }

    public int getCurrentHint() {
        return currentHint;
    }

    public void incCurrentHint() {
        this.currentHint++;
    }
}
