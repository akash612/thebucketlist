package com.tfs.akash612.thebucketlist;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class DataListObject implements Serializable {
    public String bucketListName;
    public String bucketListItem;
    public String bucketListStatus;
    public String bucketListUrl;
    public String bucketListDate;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public DataListObject() {
    }

    public DataListObject(String bucketListName, String bucketListItem, String bucketListStatus, String bucketListDate, String bucketListUrl) {
        this.bucketListName = bucketListName;
        this.bucketListItem = bucketListItem;
        this.bucketListStatus = bucketListStatus;
        this.bucketListDate = bucketListDate;
        this.bucketListUrl = bucketListUrl;

    }

    public String getBucketListName() {
        return this.bucketListName;
    }

    public String getBucketListItem() {
        return this.bucketListItem;
    }

    public String getBucketListStatus() {
        return this.bucketListStatus;
    }

    public void setBucketListStatus(String status) {
        this.bucketListStatus = status;

    }

    public void setBucketListItem(String status) {
        this.bucketListItem = status;

    }

    public void setBucketListName(String status) {
        this.bucketListName = status;

    }

    public void setBucketListDate(String date){
        this.bucketListDate = date;
    }

    public String getBucketListDate(){
        return this.bucketListDate;
    }

    public void setBucketListUrl(String url){
        this.bucketListUrl = url;
    }

    public String getBucketListUrl(){
        return this.bucketListUrl;
    }
}
