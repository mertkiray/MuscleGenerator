package com.musclegenerator.musclegenerator;

import android.graphics.Bitmap;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by egeoncel on 27.05.2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public ArrayList<MessageItem> items;
    OnCardClickListener onCardClickListener;

    public MessageAdapter(ArrayList<MessageItem> items) {
        super();
        this.items = new ArrayList<MessageItem>();

        for(int i=0;i<items.size();i++) {

            MessageItem mItem = new MessageItem();
            mItem.setMessage(items.get(i).getMessage());
            mItem.setUserName(items.get(i).getUserName());
            mItem.setBitmap(items.get(i).getBitmap());
            this.items.add(mItem);
        }

    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener){
        this.onCardClickListener = onCardClickListener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inboxcard,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        MessageItem item = items.get(position);
        holder.userName.setText(item.getUserName());
        holder.message.setText(item.getMessage());
        holder.picture.setImageBitmap(item.getBitmap());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardClickListener.onCardClicked(v,position);
            }
        });

    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

    public TextView userName,message;
        CardView cardView;
        ImageView picture;

    public ViewHolder(View itemView) {
        super(itemView);

        userName = (TextView) itemView.findViewById(R.id.nameInbox);
        message = (TextView) itemView.findViewById(R.id.messageInbox);
        cardView = (CardView) itemView.findViewById(R.id.cardInbox);
        picture = (ImageView) itemView.findViewById(R.id.pictureInbox);

    }


}


    public interface OnCardClickListener{
        void onCardClicked(View view, int position);
    }




}

