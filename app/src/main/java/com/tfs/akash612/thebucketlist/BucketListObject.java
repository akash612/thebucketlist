package com.tfs.akash612.thebucketlist;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BucketListObject implements Serializable {
    public String name;
    public ArrayList<DataListObject> dataList = new ArrayList<DataListObject>();
    public String percent;


    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public BucketListObject() {
    }

    public BucketListObject(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public ArrayList<DataListObject> addToList(String name, String item, String status, String date, String url) {
        DataListObject dataItem = new DataListObject(name, item, status, date, url);
        this.dataList.add(dataItem);
        return this.dataList;
    }

    public String getPercent(){
        float x = 0;
        this.percent = "Percentage Completed : 0%";
        if (this.dataList.size() != 0) {
            Log.d("XXX: Debud Percentage module:", String.valueOf(x));
            for (DataListObject i : this.dataList) {
                Log.d("XXX: Debud Percentage module in for:", String.valueOf(x));
                if (! i.getBucketListStatus().equals("Status : Not Done")) {
                    x++;
                    Log.d("XXX: Debud Percentage module in if:", String.valueOf(x));
                }
            }
            this.percent = "Percentage Completed : "+ String.valueOf((x/this.dataList.size())*100) + "%";
        }
        return this.percent;
    }

    public ArrayList<DataListObject> getDataList(){
        return this.dataList;
    }

    public void setDataList(ArrayList<DataListObject> dataList) {
        this.dataList = dataList;
    }

        public ArrayList<DataListObject> removeFromList(int i) {
        this.dataList.remove(i);
        return this.dataList;
    }
}
