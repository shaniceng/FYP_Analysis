package com.example.fyp_analysis;

public class HistoryActivityName {

    //private int mImageResource;
    private String mName;
    private String mDuration;
    private String mHeartrate;

    public HistoryActivityName(String name, String duration, String heartrate){
        //mImageResource = imageResource;
        mName=name;
        mDuration=duration;
        mHeartrate=heartrate;
    }

    /*public int getImageResource(){
        return mImageResource;
    } */

    public String getmName(){
        return mName;
    }
    public void setmName(String name) {
        mName = name;
    }

    public String getmDuration(){
        return mDuration;
    }
    public void setmDuration(String duration) {
        mDuration = duration;
    }

    public String getmHeartrate(){
        return mHeartrate;
    }
    public void setmHeartrate(String heartrate) {
        mHeartrate = heartrate;
    }

}
