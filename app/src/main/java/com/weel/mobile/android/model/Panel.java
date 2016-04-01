package com.weel.mobile.android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jeremy.beckman on 16-03-27.
 */
public class Panel implements Parcelable {
    private String title;
    private int icon;

    @Override
    public int describeContents() {
        return -1;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeInt(icon);
    }

    public static final Parcelable.Creator<Panel> CREATOR
            = new Parcelable.Creator<Panel>() {
        public Panel createFromParcel(Parcel in) {
            return new Panel(in);
        }

        public Panel[] newArray(int size) {
            return new Panel[size];
        }
    };

    private Panel(Parcel in) {
        title = in.readString();
        icon = in.readInt();
    }
}
