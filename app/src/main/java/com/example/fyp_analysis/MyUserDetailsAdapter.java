package com.example.fyp_analysis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyUserDetailsAdapter extends RecyclerView.Adapter<MyUserDetailsAdapter.MyUserDetailsViewHolder> {

    private ArrayList<String> mUserDate;
    private MyUserDetailsAdapter.OnDateListener mOnItemListener;

    public static class MyUserDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView UserDate;

        MyUserDetailsAdapter.OnDateListener onItemListener;
        public MyUserDetailsViewHolder(View view, MyUserDetailsAdapter.OnDateListener onItemListener) {
            super(view);
            UserDate=view.findViewById(R.id.user_date);

            this.onItemListener=onItemListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.OnDateClick(getAdapterPosition());
        }
    }

    public MyUserDetailsAdapter(ArrayList<String> mUserDate, MyUserDetailsAdapter.OnDateListener mOnItemListener){
        this.mUserDate=mUserDate;
        this.mOnItemListener=mOnItemListener;
    }

    @NonNull
    @Override
    public MyUserDetailsAdapter.MyUserDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_details_listener,parent, false);
        return new MyUserDetailsAdapter.MyUserDetailsViewHolder(v, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserDetailsAdapter.MyUserDetailsViewHolder holder, int position) {
        holder.UserDate.setText(mUserDate.get(position));
    }

    @Override
    public int getItemCount() {
        if(mUserDate!=null) {
            return mUserDate.size();
        }
        else{
            return 0;
        }
    }

    public interface OnDateListener{
        void OnDateClick(int position);
    }
}
