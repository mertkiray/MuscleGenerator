package com.musclegenerator.musclegenerator;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by MONSTER on 24.05.2017.
 */

public class Feed {


    private String heading;

    private ArrayList<Info> infoList;

    public Feed(){
        infoList= new ArrayList<>();
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public List<Info> getInfoList() {
        return infoList;
    }

    public void setInfoList(ArrayList<Info> infoList) {
        this.infoList = infoList;
    }
    public void addToInfoList(Info info) {
        infoList.add(info);
    }
}