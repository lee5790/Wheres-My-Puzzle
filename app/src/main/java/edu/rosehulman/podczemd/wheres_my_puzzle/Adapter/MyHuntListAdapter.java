package edu.rosehulman.podczemd.wheres_my_puzzle.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hunt;
import edu.rosehulman.podczemd.wheres_my_puzzle.R;

/**
 * Created by bettsld on 4/22/2018.
 */

public class MyHuntListAdapter extends RecyclerView.Adapter<MyHuntListAdapter.ViewHolder> {

    private ArrayList<Hunt> hunts;
    private HuntListCallback callback;

    public MyHuntListAdapter (ArrayList<Hunt> hunts, HuntListCallback callback) {
        this.hunts = hunts;
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
