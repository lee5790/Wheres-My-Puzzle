package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.support.v4.app.Fragment;

/**
 * Created by bettsld on 4/19/2018.
 */

public interface ViewChanger {
    public void changeView(Fragment fragment, String transactionName);
    public void changeViewAndBack(Fragment fragment, String transactionName);
}
