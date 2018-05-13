package edu.rosehulman.podczemd.wheres_my_puzzle.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by podczemd on 4/16/2018.
 */

public class User implements Parcelable{
    private String username;
    private String uid;
    private ArrayList<Hunt> currentHunts;

    public User() {
        currentHunts = new ArrayList<Hunt>();
    }

    public User(String username, String uid) {
        this.setUsername(username);
        this.setUid(uid);
        currentHunts = new ArrayList<Hunt>();
    }

    protected User(Parcel in) {
        username = in.readString();
        uid = in.readString();
        currentHunts = in.createTypedArrayList(Hunt.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(uid);
        dest.writeTypedList(currentHunts);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<Hunt> getCurrentHunts() {
        return currentHunts;
    }

    public void addCurrentHunt(Hunt currentHunt) {
        this.currentHunts.add(currentHunt);
    }

    public void removeCurrentHunt(Hunt hunt) {
        this.currentHunts.remove(hunt);
    }
}
