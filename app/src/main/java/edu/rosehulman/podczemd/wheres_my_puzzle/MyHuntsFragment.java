package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;

public class MyHuntsFragment extends Fragment implements MyHuntListAdapter.HuntListCallback{

    private User user;
    private ViewChanger viewChanger;

    private RecyclerView recyclerView;
    private Button backButton;
    private Button createButton;

    public MyHuntsFragment() {
        // Required empty public constructor
    }


    public static MyHuntsFragment newInstance(User user) {
        MyHuntsFragment fragment = new MyHuntsFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_hunts, container, false);

        backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeViewAndBack(CurrentHuntsFragment.newInstance(user));
            }
        });

        createButton = view.findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeView(CreateHuntFragment.newInstance(user,new Hunt(user.getUsername())), "Create new hunt");
            }
        });

        recyclerView = view.findViewById(R.id.huntRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MyHuntListAdapter(user.getCreatedHunts(), this));

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

    @Override
    public void huntSelected(Hunt hunt) {
        viewChanger.changeView(CreateHuntFragment.newInstance(user,hunt), "Edit hunt");
    }
}
