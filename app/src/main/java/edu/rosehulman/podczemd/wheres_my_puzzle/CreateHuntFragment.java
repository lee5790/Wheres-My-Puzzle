package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;


public class CreateHuntFragment extends Fragment {
    public CreateHuntFragment() {
        // Required empty public constructor
    }
    public static CreateHuntFragment newInstance() {
        CreateHuntFragment fragment = new CreateHuntFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, doc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_hunt, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
