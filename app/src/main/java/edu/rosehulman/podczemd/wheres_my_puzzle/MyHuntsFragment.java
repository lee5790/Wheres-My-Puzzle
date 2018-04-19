package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyHuntsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyHuntsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyHuntsFragment extends Fragment {

    private ViewChanger viewChanger;
    private Button createButton;

    public MyHuntsFragment() {
        // Required empty public constructor
    }


    public static MyHuntsFragment newInstance() {
        MyHuntsFragment fragment = new MyHuntsFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_hunts, container, false);

        createButton = view.findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeView(new CreateHuntFragment(), "Create new hunt");
            }
        });

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
