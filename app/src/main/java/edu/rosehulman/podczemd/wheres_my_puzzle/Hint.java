package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by podczemd on 4/16/2018.
 */

public class Hint implements Parcelable{
    private String hint;
    private double latitude;
    private double longitude;
    private String finishMessage;

    public Hint () {

    }

    protected Hint(Parcel in) {
        hint = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        finishMessage = in.readString();
    }

    public static final Creator<Hint> CREATOR = new Creator<Hint>() {
        @Override
        public Hint createFromParcel(Parcel in) {
            return new Hint(in);
        }

        @Override
        public Hint[] newArray(int size) {
            return new Hint[size];
        }
    };

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFinishMessage() {
        return finishMessage;
    }

    public void setFinishMessage(String finishMessage) {
        this.finishMessage = finishMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hint);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(finishMessage);
    }
}
