package com.musclegenerator.musclegenerator;

/**
 * Created by MONSTER on 28.05.2017.
 */

public class WorkoutListElement {
    private String name;
    private int id;
    private String creationDate;

    public WorkoutListElement(String name, int id){
        setId(id);
        setName(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
