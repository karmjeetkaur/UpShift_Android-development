package com.upshft.upshiftapp.modal;

import java.io.Serializable;

/**
 * Created by new on 2/16/2017.
 */

public class ImageModel implements Serializable{

    public String image_path;

    public ImageModel(String image_path) {
        this.image_path = image_path;
    }


    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
