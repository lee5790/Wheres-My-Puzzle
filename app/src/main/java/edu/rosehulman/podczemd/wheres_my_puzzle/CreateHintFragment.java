package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;


public class CreateHintFragment extends Fragment {
    private User user;
    private ViewChanger viewChanger;


    public CreateHintFragment() {
        // Required empty public constructor
    }

    public static CreateHintFragment newInstance(User user) {
        CreateHintFragment fragment = new CreateHintFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_hint, container, false);
        EditText hintDescription = view.findViewById(R.id.hintDetailEditText);
        hintDescription.setText(user.getPassword());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewChanger) {
            viewChanger = (ViewChanger) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement ViewChanger");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
