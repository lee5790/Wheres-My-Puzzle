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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.rosehulman.podczemd.wheres_my_puzzle.Adapter.HuntListAdapter;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.ViewChanger;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hunt;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.User;
import edu.rosehulman.podczemd.wheres_my_puzzle.R;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinNewHuntFragment extends Fragment implements HuntListAdapter.HuntListCallback{
    private final int MAX_LIST_SIZE = 8;

    private User user;
    private int pageNum;
    private ArrayList<Hunt> hunts;
    private HuntListAdapter adapter;
    private ViewChanger viewChanger;

    private RecyclerView recyclerView;
    private Button prevbutton;
    private Button nextButton;


    public JoinNewHuntFragment() {
        // Required empty public constructor
    }

    public static JoinNewHuntFragment newInstance(String uid) {
        JoinNewHuntFragment fragment = new JoinNewHuntFragment();
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
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        pageNum = 0;
        hunts = new ArrayList<Hunt>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_new_hunt, container, false);
        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeViewAndBack(CurrentHuntsFragment.newInstance(user.getUid()));
            }
        });

        recyclerView = view.findViewById(R.id.huntRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new HuntListAdapter(new ArrayList<Hunt>(), this);
        recyclerView.setAdapter(adapter);

        prevbutton = view.findViewById(R.id.prevButton);
        prevbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(false);
            }
        });

        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(true);
            }
        });

        updateData(true);
        return view;
    }

    private void updateData(boolean forwards) {
        Query query;
        if(hunts.size() == 0) {
            query = FirebaseDatabase.getInstance().getReference().child("hunts").orderByChild("key").limitToFirst(MAX_LIST_SIZE);
        }
        else if (forwards){
            pageNum++;
            query = FirebaseDatabase.getInstance().getReference().child("hunts").orderByChild("key").startAt(hunts.get(MAX_LIST_SIZE - 1).getKey()).limitToFirst(MAX_LIST_SIZE);
        }
        else {
            pageNum--;
            query = FirebaseDatabase.getInstance().getReference().child("hunts").orderByChild("key").endAt(hunts.get(0).getKey()).limitToLast(MAX_LIST_SIZE);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hunts = new ArrayList<Hunt>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Hunt hunt = snapshot.getValue(Hunt.class);
                    hunts.add(hunt);
                }
                int maxNum = hunts.size();
                if (maxNum > MAX_LIST_SIZE - 1) {
                    maxNum = MAX_LIST_SIZE - 1;
                }
                adapter.updateData(hunts.subList(0, maxNum));
                prevbutton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                if(pageNum == 0) {
                    prevbutton.setVisibility(View.GONE);
                }
                if (hunts.size() < MAX_LIST_SIZE) {
                    nextButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    }
}
