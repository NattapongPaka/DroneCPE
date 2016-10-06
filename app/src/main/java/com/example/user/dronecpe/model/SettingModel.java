package com.example.user.dronecpe.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dev on 5/10/2559.
 */

public class SettingModel implements Parcelable {

    private String id;
    private String title;
    private String textValue;

    public SettingModel(String id,String title,String textValue){
        this.id = id;
        this.title = title;
        this.textValue = textValue;
    }

    private SettingModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        textValue = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(textValue);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SettingModel> CREATOR = new Creator<SettingModel>() {
        @Override
        public SettingModel createFromParcel(Parcel in) {
            return new SettingModel(in);
        }

        @Override
        public SettingModel[] newArray(int size) {
            return new SettingModel[size];
        }
    };

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
