package edu.rosehulman.podczemd.wheres_my_puzzle;

import java.util.ArrayList;

/**
 * Created by podczemd on 4/16/2018.
 */

public class User {
    String username;
    String password;
    ArrayList<Hunt> currentHunts;
    ArrayList<Hunt> createdHunts;

    User(String username, String password){
        this.username=username;
        this.password=password;
        currentHunts = new ArrayList<>();
        createdHunts = new ArrayList<>();
    }
}
