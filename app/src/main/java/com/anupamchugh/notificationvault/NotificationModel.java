package com.anupamchugh.notificationvault;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationModel implements Parcelable, Comparable, Cloneable {

    public String title, body, packageName, sbnKey;
    public long timeStamp;
    byte[] notificationIcon;


    public NotificationModel(String title, String body, long timeStamp, byte[] notificationIcon, String sbnKey, String packageName) {
        this.title = title;
        this.body = body;
        this.packageName = packageName;
        this.sbnKey = sbnKey;
        this.timeStamp = timeStamp;
        this.notificationIcon = notificationIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeString(this.packageName);
        dest.writeString(this.sbnKey);
        dest.writeLong(this.timeStamp);
        dest.writeByteArray(this.notificationIcon);

    }

    protected NotificationModel(Parcel in) {
        this.title = in.readString();
        this.body = in.readString();
        this.packageName = in.readString();
        this.sbnKey = in.readString();
        this.timeStamp = in.readLong();
        this.notificationIcon = in.createByteArray();
    }

    public static final Parcelable.Creator<NotificationModel> CREATOR = new Parcelable.Creator<NotificationModel>() {
        @Override
        public NotificationModel createFromParcel(Parcel source) {
            return new NotificationModel(source);
        }

        @Override
        public NotificationModel[] newArray(int size) {
            return new NotificationModel[size];
        }
    };

    @Override
    public int compareTo(Object o) {
        NotificationModel compare = (NotificationModel) o;

        if (compare.timeStamp == this.timeStamp && compare.title.equals(this.title) && compare.sbnKey.equals(this.sbnKey)) {
            return 0;
        }
        return 1;
    }

}
