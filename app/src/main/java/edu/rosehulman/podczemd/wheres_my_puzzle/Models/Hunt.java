package edu.rosehulman.podczemd.wheres_my_puzzle.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by podczemd on 4/16/2018.
 */

public class Hunt implements Parcelable{
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


    protected Hunt(Parcel in) {
        title = in.readString();
        creator = in.readString();
        description = in.readString();
        hints = in.createTypedArrayList(Hint.CREATOR);
        finalMessage = in.readString();
        currentHint = in.readInt();
    }

    public static final Creator<Hunt> CREATOR = new Creator<Hunt>() {
        @Override
        public Hunt createFromParcel(Parcel in) {
            return new Hunt(in);
        }

        @Override
        public Hunt[] newArray(int size) {
            return new Hunt[size];
        }
    };

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

    public void removeHint(Hint hint) {
        this.hints.remove(hint);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(creator);
        dest.writeString(description);
        dest.writeTypedList(hints);
        dest.writeString(finalMessage);
        dest.writeInt(currentHint);
    }
}
