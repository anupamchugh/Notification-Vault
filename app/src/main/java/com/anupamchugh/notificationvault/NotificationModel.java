package com.anupamchugh.notificationvault;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;

public class NotificationModel implements Parcelable, Comparable, Cloneable {

    public String title, body, packageName, sbnKey;
    public long timeStamp;
    PendingIntent pendingIntent;
    byte[] serializedPersistentPendingIntent;

    public NotificationModel(String title, String body, long timeStamp, String sbnKey, String packageName, PendingIntent pendingIntent) {
        this.title = title;
        this.body = body;
        this.packageName = packageName;
        this.sbnKey = sbnKey;
        this.timeStamp = timeStamp;
        this.pendingIntent = pendingIntent;
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




        /*pendingIntent.writeToParcel(dest, 0);
        serializedPersistentPendingIntent = dest.marshall();*/


    }

    protected NotificationModel(Parcel in) {
        this.title = in.readString();
        this.body = in.readString();
        this.packageName = in.readString();
        this.sbnKey = in.readString();
        this.timeStamp = in.readLong();



        /*in.unmarshall(serializedPersistentPendingIntent, 0, serializedPersistentPendingIntent.length);
        pendingIntent = (PendingIntent) in.readValue(PendingIntent.class.getClassLoader());*/
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
