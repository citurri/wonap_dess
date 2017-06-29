package com.develop.android.wonap.test;

/**
 * Created by Goloso on 28/6/2017.
 */

public class GetSet {
    int img;
    String text;

    public GetSet(int img, String text) {
        this.img = img;
        this.text = text;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}