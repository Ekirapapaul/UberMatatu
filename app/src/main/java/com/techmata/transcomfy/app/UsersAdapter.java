package com.techmata.transcomfy.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Sparks on 26/03/2016.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolderActTime> {

    //contains the list of posts
    //private ArrayList<String> mATLists = new ArrayList<String>();
    private ArrayList<String> myUsers = new ArrayList<String>();
    private LayoutInflater mInflater;
    Context mcontext;
    ViewHolderActTime viewHolder;

    public UsersAdapter(Context context, ArrayList<String> atLists) {
        this.mcontext = context;
        myUsers = atLists;
        mInflater = LayoutInflater.from(context);
    }

    public void addUser(int a, String email) {
        //this.mATLists.add(a);
        //add everything
        myUsers.add(a,email);
        //update the adapter to reflect the new set of posts
        notifyDataSetChanged();
    }
    public ArrayList<String > getTimes(){
       return myUsers;
    }

    @Override
    public ViewHolderActTime onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.user_item, parent, false);
        final ViewHolderActTime vHolder = new ViewHolderActTime(view,mcontext);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderActTime holder, final int position) {

        this.viewHolder = holder;
        holder.tvEmail.setText(myUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return myUsers.size();
    }

    public boolean removeAt(int position) {
            myUsers.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, myUsers.size());
            return true;
    }

    class ViewHolderActTime extends RecyclerView.ViewHolder {

        ImageButton btnClose;
        TextView tvEmail;

        public ViewHolderActTime(final View itemView, final Context mcontext) {
            super(itemView);
            btnClose = (ImageButton) itemView.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    removeAt(getAdapterPosition());
                    v.setVisibility(View.VISIBLE);
                }
            });

            tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);

        }
    }
}

