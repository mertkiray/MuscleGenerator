package com.musclegenerator.musclegenerator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mert on 26.05.2017.
 */

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    List<AddFriendItem> itemsa;
    OnCardClickListener onCardClickListener;
    public FriendRequestAdapter(ArrayList<AddFriendItem> items ){
        super();
        itemsa = new ArrayList<AddFriendItem>();
        for(int i =0; i<items.size(); i++){
            AddFriendItem item = new AddFriendItem();
            item.setName(items.get(i).getName());
            item.setAge(items.get(i).getAge());
            item.setBitmap(items.get(i).getBitmap());
            item.setUid(items.get(i).getUid());
            itemsa.add(item);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userrequestcard, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AddFriendItem list =  itemsa.get(position);
        holder.name.setText(list.getName());
        holder.age.setText(String.valueOf(list.getAge()));
        holder.photo.setImageBitmap(list.getBitmap());
        holder.addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardClickListener.OnCardClicked(v,position);
            }
        });

        holder.rejectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardClickListener.OnRejectClicked(v,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsa.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView age;
        public ImageView photo;
        public Button addbutton;
        public Button rejectbutton;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.score);
            photo = (ImageView) itemView.findViewById(R.id.workoutphoto);
            addbutton = (Button) itemView.findViewById(R.id.addfriendbutton);
            rejectbutton = (Button) itemView.findViewById(R.id.rejectFriend);

        }
    }

    public interface OnCardClickListener {
        void OnCardClicked(View view, int position);
        void OnRejectClicked(View view,int position);
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

}
