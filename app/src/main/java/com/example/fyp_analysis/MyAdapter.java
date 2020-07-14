package com.example.fyp_analysis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  { //implements Filterable
    private ArrayList<String> mUserProfileSet;
    private ArrayList<String> mUserProfileSetFULL;
    private ArrayList<String> mUserIDSet;
    private OnItemListener mOnItemListener;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView UserName, UserID;

        OnItemListener onItemListener;
        public MyViewHolder(View view, OnItemListener onItemListener) {
            super(view);
            UserID=view.findViewById(R.id.UserIDMain);
            UserName=view.findViewById(R.id.UserNameMain);

            this.onItemListener=onItemListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.OnItemClick(getAdapterPosition());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<String> mUserIDSet, ArrayList<String> mUserProfileSet, OnItemListener mOnItemListener){
       this.mUserProfileSet=mUserProfileSet;
       this.mUserIDSet=mUserIDSet;
       this.mOnItemListener=mOnItemListener;
       mUserProfileSetFULL = new ArrayList<>(mUserProfileSet);
    }



    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listener,parent, false);
        return new MyViewHolder(v, mOnItemListener);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.UserID.setText(mUserIDSet.get(position));
        holder.UserName.setText(mUserProfileSet.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUserProfileSet.size();
    }

    public interface OnItemListener{
        void OnItemClick(int position);
    }

    public void filterList(ArrayList<String> filteredList) {
        mUserProfileSet = filteredList;
        notifyDataSetChanged();

    }
}
