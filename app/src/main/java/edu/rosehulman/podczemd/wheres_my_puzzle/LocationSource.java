package edu.rosehulman.podczemd.wheres_my_puzzle;

/**
 * Created by bettsld on 4/29/2018.
 */

interface LocationSource {
    public void subscribe(LocationObserver obs);
    public void unSubscribe(LocationObserver obs);
}
