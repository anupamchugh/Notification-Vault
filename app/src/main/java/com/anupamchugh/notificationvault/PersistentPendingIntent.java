package com.anupamchugh.notificationvault;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PersistentPendingIntent implements Parcelable
{
    private enum PendingIntentType{SERVICE, BROADCAST, ACTIVITY}
    @NonNull
    private final PendingIntentType pendingIntentType;
    protected final int requestCode;
    protected final int flags;
    @NonNull
    protected final Intent intent;

    private PersistentPendingIntent(@NonNull PendingIntentType pendingIntentType, int requestCode, @NonNull Intent intent, int flags)
    {
        this.pendingIntentType = pendingIntentType;
        this.flags = flags;
        this.intent = intent;
        this.requestCode = requestCode;
    }

    @Nullable
    public PendingIntent getPendingIntent(@NonNull Context context)
    {
        PendingIntent pendingIntent = null;
        switch (pendingIntentType)
        {
            case SERVICE:
                pendingIntent = PendingIntent.getService(context, requestCode, intent, flags);
                break;
            case BROADCAST:
                pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags);
                break;
            case ACTIVITY:
                pendingIntent = PendingIntent.getActivity(context, requestCode, intent, flags);
                break;
        }
        return pendingIntent;
    }


    public static PersistentPendingIntent getService(int requestCode, @NonNull Intent intent, int flags)
    {
        return new PersistentPendingIntent(PendingIntentType.SERVICE, requestCode, intent, flags);
    }

    public static PersistentPendingIntent getActivity(int requestCode, @NonNull Intent intent, int flags)
    {
        return new PersistentPendingIntent(PendingIntentType.ACTIVITY, requestCode, intent, flags);
    }

    public static PersistentPendingIntent getBroadcast(int requestCode, @NonNull Intent intent, int flags)
    {
        return new PersistentPendingIntent(PendingIntentType.BROADCAST, requestCode, intent, flags);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(this.pendingIntentType.ordinal());
        dest.writeInt(this.requestCode);
        dest.writeInt(this.flags);
        dest.writeParcelable(this.intent, flags);
    }

    protected PersistentPendingIntent(Parcel in)
    {
        int tmpPendingIntentType = in.readInt();
        this.pendingIntentType = PendingIntentType.values()[tmpPendingIntentType];
        this.requestCode = in.readInt();
        this.flags = in.readInt();
        this.intent = in.readParcelable(Intent.class.getClassLoader());
    }

    public static final Creator<PersistentPendingIntent> CREATOR = new Creator<PersistentPendingIntent>()
    {
        @Override
        public PersistentPendingIntent createFromParcel(Parcel source)
        {
            return new PersistentPendingIntent(source);
        }

        @Override
        public PersistentPendingIntent[] newArray(int size)
        {
            return new PersistentPendingIntent[size];
        }
    };
}
