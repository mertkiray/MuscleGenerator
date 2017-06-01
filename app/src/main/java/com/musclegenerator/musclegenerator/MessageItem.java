package com.musclegenerator.musclegenerator;

import android.graphics.Bitmap;

/**
 * Created by egeoncel on 27.05.2017.
 */

public class MessageItem {

    private String userName,message;
    private Bitmap bitmap;
    public Bitmap getBitmap() { return bitmap;}
    public void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}
    public String getUserName() { return userName;}
    public void setUserName(String userName) {this.userName = userName;}
    public String getMessage() { return message;}
    public void setMessage(String message) {this.message = message;}


}
