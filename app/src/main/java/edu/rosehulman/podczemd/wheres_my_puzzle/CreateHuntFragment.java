package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;


public class CreateHuntFragment extends Fragment {

    private User user;
    private ViewChanger viewChanger;

    private Button cancelButton;
    private Button deleteButton;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button addHuntButton;
    private EditText finishMessageEditText;

    public CreateHuntFragment() {
    }

    public static CreateHuntFragment newInstance(User user) {
        CreateHuntFragment fragment = new CreateHuntFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_hunt, container, false);
        cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeViewAndBack(MyHuntsFragment.newInstance(user), "My Hunts");
            }
        });

        deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO delete from created hunts
                viewChanger.changeViewAndBack(MyHuntsFragment.newInstance(user), "My Hunts");
            }
        });

        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);

        addHuntButton = view.findViewById(R.id.addHuntButton);
        addHuntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeView(CreateHintFragment.newInstance(user), "Create new Hint");
            }
        });

        finishMessageEditText = view.findViewById(R.id.finishMessageEditText);
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
