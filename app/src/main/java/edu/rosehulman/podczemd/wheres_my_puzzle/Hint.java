package edu.rosehulman.podczemd.wheres_my_puzzle;

/**
 * Created by podczemd on 4/16/2018.
 */

public class Hint {
    private String hint;
    private double latitude;
    private double longitude;
    private String finishMessage;

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
}
