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

import edu.rosehulman.podczemd.wheres_my_puzzle.Adapter.HuntListAdapter;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hunt;
import edu.rosehulman.podczemd.wheres_my_puzzle.R;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.User;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.ViewChanger;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentHuntsFragment extends Fragment implements HuntListAdapter.HuntListCallback{

    private User user;
    private ViewChanger viewChanger;

    private RecyclerView recyclerView;
    private Button myHuntsButton;
    private Button joinHuntButton;


    public CurrentHuntsFragment() {
        // Required empty public constructor
    }

    public static CurrentHuntsFragment newInstance(String uid) {
        CurrentHuntsFragment fragment = new CurrentHuntsFragment();
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
                        recyclerView.setAdapter(new HuntListAdapter(user.getCurrentHunts(), CurrentHuntsFragment.this));
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_hunts, container, false);

        recyclerView = view.findViewById(R.id.currentHuntsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(user != null) {
            recyclerView.setAdapter(new HuntListAdapter(user.getCurrentHunts(), this));
        }
        myHuntsButton = view.findViewById(R.id.myHuntsButton);
        myHuntsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeView(MyHuntsFragment.newInstance(user.getUid()), "My Hunts");
            }
        });

        joinHuntButton = view.findViewById(R.id.joinHuntButton);
        joinHuntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO change to join hunt screen next sprint
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

    @Override
    public void huntSelected(Hunt hunt) {
        viewChanger.changeView(ActiveHuntFragment.newInstance(user, hunt), "Active Hunt");
    }
}
