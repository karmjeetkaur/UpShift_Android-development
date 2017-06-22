package com.upshft.upshiftapp.modal;

import java.io.Serializable;

/**
 * Created by new on 12/14/2016.
 */
public class Grid_Modal implements Serializable {
    String name;
    boolean mFalg;
    Integer image;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setmFalg(boolean mFalg) {
        this.mFalg = mFalg;
    }

    public boolean ismFalg() {

        return mFalg;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }
}
