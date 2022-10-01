package com.example.miwok_app_master;

public class word {


    private String  mDefaultTranslation;
    private String mMiwokTranslation;
    private int mImageResourceID = NO_RESOURCE_IMAGE;
    private static final int NO_RESOURCE_IMAGE = -1;
    private int maudioResourceID;



    public word(String DefaultTranslation, String MiwokTranslation, int audioResoruceID){
        mDefaultTranslation = DefaultTranslation;
        mMiwokTranslation = MiwokTranslation;
        maudioResourceID = audioResoruceID;

    }

    //imageResourceID is drawable resource id for image asset
    public word(String DefaultTranslation, String MiwokTranslation, int imageResourceID, int audioResoruceID){
        mDefaultTranslation = DefaultTranslation;
        mMiwokTranslation = MiwokTranslation;
        mImageResourceID = imageResourceID;
        maudioResourceID = audioResoruceID;
    }

    //get default translation of the word
    public String getDefaultTranslation(){
        return mDefaultTranslation;
    }

    //get Miwok translation of the word
    public String getMiwokTranslation(){
        return mMiwokTranslation;
    }

    public int getImageResourceID(){
        return mImageResourceID;
    }



    public boolean hasImage(){
        return mImageResourceID!=NO_RESOURCE_IMAGE;
    }

    public int getaudioResourceID(){
        return maudioResourceID;
    }
}
