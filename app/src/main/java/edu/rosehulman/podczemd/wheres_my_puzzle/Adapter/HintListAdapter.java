package edu.rosehulman.podczemd.wheres_my_puzzle.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hint;
import edu.rosehulman.podczemd.wheres_my_puzzle.R;

/**
 * Created by bettsld on 4/22/2018.
 */

public class HintListAdapter extends RecyclerView.Adapter<HintListAdapter.ViewHolder> {

    private ArrayList<Hint> hints;
    private HintListCallback callback;

    public HintListAdapter(ArrayList<Hint> hints, HintListCallback callback) {
        this.hints = hints;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_hint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Hint hint = hints.get(position);
        holder.hint = hint;
        holder.hintText.setText(hint.getHint());
    }

    @Override
    public int getItemCount() {
        return hints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Hint hint;
        public TextView hintText;

        public ViewHolder(View itemView) {
            super(itemView);
            hintText = itemView.findViewById(R.id.hintTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.hintSelected(hint);
                }
            });
        }
    }

    public interface HintListCallback {
        public void hintSelected(Hint hint);
    }
}
