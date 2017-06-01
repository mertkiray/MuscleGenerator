package com.musclegenerator.musclegenerator;


import android.graphics.Bitmap;

public class AddFriendItem {
    private String name,uid,token;
    private int age;
    private Bitmap bitmap;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge(){
        return age;
    }
    public void setAge(int age){
        this.age = age;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public String getUid(){
        return uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }
    public String getToken(){return token;}
    public void setToken(String token){this.token = token;}

}
