package edu.rosehulman.podczemd.wheres_my_puzzle.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hunt;
import edu.rosehulman.podczemd.wheres_my_puzzle.R;

/**
 * Created by bettsld on 4/22/2018.
 */

public class HuntListAdapter extends RecyclerView.Adapter<HuntListAdapter.ViewHolder> {

    private List<Hunt> hunts;
    private HuntListCallback callback;

    public HuntListAdapter(List<Hunt> hunts, HuntListCallback callback) {
        this.hunts = hunts;
        this.callback = callback;
    }

    public HuntListAdapter(String uid, HuntListCallback callback) {
        hunts = new ArrayList<Hunt>();
        Query huntsRef = FirebaseDatabase.getInstance().getReference().child("hunts").orderByChild("creatorUID").equalTo(uid);
        huntsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot huntSnapshot : dataSnapshot.getChildren()) {
                    Hunt hunt = huntSnapshot.getValue(Hunt.class);
                    hunts.add(hunt);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_hunt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Hunt hunt = hunts.get(position);
        holder.hunt = hunt;
        holder.huntTitleText.setText(hunt.getTitle());
    }

    @Override
    public int getItemCount() {
        return hunts.size();
    }

    public void updateData(List<Hunt> newHunts) {
        hunts = newHunts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Hunt hunt;
        public TextView huntTitleText;

        public ViewHolder(View itemView) {
            super(itemView);
            huntTitleText = itemView.findViewById(R.id.huntTitleTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.huntSelected(hunt);
                }
            });
        }
    }

    public interface HuntListCallback {
        public void huntSelected(Hunt hunt);
    }
}
