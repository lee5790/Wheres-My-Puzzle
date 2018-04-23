package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_HINT;
import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_HUNT;
import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;


public class CreateHintFragment extends Fragment {
    private Button cancelButton;
    private Button deleteButton;
    private Button finishButton;
    private User user;
    private Hunt hunt;
    private Hint hint;
    private ViewChanger viewChanger;
    EditText hintText;
    EditText latitudeText;
    EditText longitudeText;
    EditText finishText;


    public CreateHintFragment() {
        // Required empty public constructor
    }

    public static CreateHintFragment newInstance(User user, Hunt hunt, Hint hint) {
        CreateHintFragment fragment = new CreateHintFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        args.putParcelable(ARG_HUNT, hunt);
        args.putParcelable(ARG_HINT,hint);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
            hunt = getArguments().getParcelable(ARG_HUNT);
            hint = getArguments().getParcelable(ARG_HINT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_hint, container, false);
        EditText hintDescription = view.findViewById(R.id.hintDetailEditText);
        hintDescription.setText(user.getPassword());
        cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeViewAndBack(CreateHuntFragment.newInstance(user,hunt), "Create Hunts");
            }
        });

        deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hunt.removeHint(hint);
                viewChanger.changeViewAndBack(CreateHuntFragment.newInstance(user,hunt), "Create Hunts");
            }
        });

        finishButton = view.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finalizeHint(v);
            }
        });
        this.hintText = view.findViewById(R.id.hintDetailEditText);
        this.hintText.setText(hint.getHint());

        this.latitudeText = view.findViewById(R.id.latitudeEditText);
        this.latitudeText.setText("" + hint.getLatitude());

        this.longitudeText = view.findViewById(R.id.longitudeEditText);
        this.longitudeText.setText("" + hint.getLongitude());

        this.finishText = view.findViewById(R.id.hintFinishMessageEditText);
        this.finishText.setText(hint.getFinishMessage());
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

    private void finalizeHint(View view){
        this.hint.setHint(String.valueOf(hintText.getText()));
        this.hint.setLatitude(Integer.parseInt(latitudeText.getText().toString()));
        this.hint.setLongitude(Integer.parseInt(longitudeText.getText().toString()));
        this.hint.setFinishMessage(String.valueOf(finishText.getText()));
        if (!hunt.getHints().contains(hint)) {
            hunt.addHint(hint);
        }
        viewChanger.changeViewAndBack(CreateHuntFragment.newInstance(user,hunt), "Create Hunts");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
