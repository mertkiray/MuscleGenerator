package com.musclegenerator.musclegenerator;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.LongClick;
import com.mindorks.placeholderview.annotations.Position;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.expand.ChildPosition;
import com.mindorks.placeholderview.annotations.expand.ParentPosition;

import java.util.ArrayList;

/**
 * Created by MONSTER on 24.05.2017.
 */


@Layout(R.layout.feed_item_three)
public class InfoViewThree {



    @ParentPosition
    private int mParentPosition;

    @ChildPosition
    private int mChildPosition;

    @View(R.id.titleTxt)
    private TextView titleTxt;

    @View(R.id.captionTxt)
    private TextView captionTxt;

    @View(R.id.checkBox)
    private CheckBox checkBox;

    @View(R.id.imageView)
    private ImageView imageView;

    private Info mInfo;
    private Context mContext;
    private boolean checked;


    public InfoViewThree(Context context, Info info ) {
        mContext = context;
        mInfo = info;
        checked=false;


    }

    @Resolve
    private void onResolved() {
        titleTxt.setText(mInfo.getTitle());
        captionTxt.setText(mInfo.getDifficulty());
        Glide.with(mContext).load(mInfo.getImageUrl()).into(imageView);
    }






}