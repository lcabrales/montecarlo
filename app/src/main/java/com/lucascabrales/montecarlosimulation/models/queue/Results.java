package com.lucascabrales.montecarlosimulation.models.queue;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lucascabrales on 12/3/17.
 */

public class Results implements Parcelable {
    public static final String KEY = "ResultsKey";

    public String averageWaitTime;
    public String queueProbability;
    public String averageQueueLenght;
    public String maxQueueLenght;
    public String averageTotalTime;
    public String queueLenght;
    public String totalQty;
    public String maxWaitTime;

    public Results(){

    }

    protected Results(Parcel in) {
        averageWaitTime = in.readString();
        queueProbability = in.readString();
        averageQueueLenght = in.readString();
        maxQueueLenght = in.readString();
        averageTotalTime = in.readString();
        queueLenght = in.readString();
        totalQty = in.readString();
        maxWaitTime = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(averageWaitTime);
        dest.writeString(queueProbability);
        dest.writeString(averageQueueLenght);
        dest.writeString(maxQueueLenght);
        dest.writeString(averageTotalTime);
        dest.writeString(queueLenght);
        dest.writeString(totalQty);
        dest.writeString(maxWaitTime);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Results> CREATOR = new Parcelable.Creator<Results>() {
        @Override
        public Results createFromParcel(Parcel in) {
            return new Results(in);
        }

        @Override
        public Results[] newArray(int size) {
            return new Results[size];
        }
    };
}
