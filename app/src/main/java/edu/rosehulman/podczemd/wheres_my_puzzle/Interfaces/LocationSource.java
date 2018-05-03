package edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces;

import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.LocationObserver;

/**
 * Created by bettsld on 4/29/2018.
 */

public interface LocationSource {
    public void subscribe(LocationObserver obs);
    public void unSubscribe(LocationObserver obs);
}
