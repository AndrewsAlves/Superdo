package com.andysapps.superdo.todo.adapters.taskrecyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.andysapps.superdo.todo.R;
import com.andysapps.superdo.todo.manager.FirestoreManager;
import com.andysapps.superdo.todo.model.User;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrews on 15,August,2019
 */

public class RanksRecyclerAdapter extends RecyclerView.Adapter<RanksRecyclerAdapter.PlaceViewHolder> {

    private static final String TAG = "BucketRecyclerAdapter";
    private int[] trophyList;
    private int[] trophyScrorePoints;
    private User user;

    private Context context;

    public RanksRecyclerAdapter(Context context, int[] trophyList, int[] trophyScrorePoints ) {
        this.trophyList = trophyList;
        this.trophyScrorePoints = trophyScrorePoints;
        this.context = context;
        user = FirestoreManager.getInstance().user;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_esprit_rank, viewGroup, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int i) {

        int points = user.getEspritPoints();

        holder.lock.setVisibility(View.VISIBLE);
        holder.tvTrophyPoints.setText("Reach "+ trophyScrorePoints[i]);
        holder.trophy.setVisibility(View.GONE);

        if (points >= trophyScrorePoints[i]) {
            holder.lock.setVisibility(View.GONE);
            holder.trophy.setVisibility(View.VISIBLE);
            holder.ivTrophy.setImageResource(trophyList[i]);
        }
    }

    @Override
    public int getItemCount() {
        return trophyList.length;
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cl_rank)
        ConstraintLayout trophy;

        @BindView(R.id.iv_trophy)
        ImageView ivTrophy;

        @BindView(R.id.rl_lock)
        RelativeLayout lock;

        @BindView(R.id.tv_rank_score)
        TextView tvTrophyPoints;

        public View item;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
            ButterKnife.bind(this,itemView);
        }
    }
}
