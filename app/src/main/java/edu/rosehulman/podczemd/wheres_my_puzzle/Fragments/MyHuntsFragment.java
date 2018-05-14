package edu.rosehulman.podczemd.wheres_my_puzzle.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hunt;
import edu.rosehulman.podczemd.wheres_my_puzzle.Adapter.HuntListAdapter;
import edu.rosehulman.podczemd.wheres_my_puzzle.R;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.User;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.ViewChanger;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;

public class MyHuntsFragment extends Fragment implements HuntListAdapter.HuntListCallback {

    private User user;
    private ViewChanger viewChanger;

    private RecyclerView recyclerView;
    private Button backButton;
    private Button createButton;

    public MyHuntsFragment() {
        // Required empty public constructor
    }


    public static MyHuntsFragment newInstance(String uid) {
        MyHuntsFragment fragment = new MyHuntsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String uid = getArguments().getString(ARG_USER);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    user.setUid(dataSnapshot.getKey());
                    if(recyclerView != null) {
                        recyclerView.setAdapter(new HuntListAdapter(user.getUid(), MyHuntsFragment.this));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
                viewChanger.changeViewAndBack(CurrentHuntsFragment.newInstance(user.getUid()));
            }
        });

        createButton = view.findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeView(CreateHuntFragment.newInstance(user.getUid(), new Hunt(user)), "Create new hunt");
            }
        });

        recyclerView = view.findViewById(R.id.huntRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        if(user != null) {
            recyclerView.setAdapter(new HuntListAdapter(user.getUid(), this));
        }
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
        viewChanger.changeView(CreateHuntFragment.newInstance(user.getUid(), hunt), "Edit hunt");
    }
}
