package edu.rosehulman.podczemd.wheres_my_puzzle.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.podczemd.wheres_my_puzzle.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindNewHuntFragment extends Fragment {


    public FindNewHuntFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_new_hunt, container, false);
    }

}
