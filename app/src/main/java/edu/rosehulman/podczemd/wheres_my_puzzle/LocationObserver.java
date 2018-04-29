package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.location.Location;

/**
 * Created by bettsld on 4/29/2018.
 */

interface LocationObserver {
    public void updateLocation(Location location);
}
