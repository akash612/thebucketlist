package com.tfs.akash612.thebucketlist;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class DrawerMenuObject implements Serializable {
    public String menuItem;
    public Integer menuImage;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public DrawerMenuObject() {
    }

    public DrawerMenuObject(String menuItem, Integer menuImage) {
        this.menuItem = menuItem;
        this.menuImage = menuImage;

    }

    public String getMenuItem() {
        return this.menuItem;
    }

    public Integer getMenuImage() {
        return this.menuImage;
    }

}