package com.lucascabrales.montecarlosimulation.models.queue;

import android.os.Parcel;
import android.os.Parcelable;

import com.lucascabrales.montecarlosimulation.enums.Distribution;
import com.lucascabrales.montecarlosimulation.enums.TimeUnit;

/**
 * Created by lucascabrales on 12/3/17.
 */

public class SimParams implements Parcelable {
    public static final String QUEUE_KEY = "QueueParamsKey";
    public static final String SERVER_KEY = "ServerParamsKey";

    public Double mean;
    public Double deviance;
    public Double max;
    public Distribution distribution;
    public TimeUnit timeUnit;

    public SimParams(){

    }

    //region Parcelable
    protected SimParams(Parcel in) {
        mean = in.readByte() == 0x00 ? null : in.readDouble();
        deviance = in.readByte() == 0x00 ? null : in.readDouble();
        max = in.readByte() == 0x00 ? null : in.readDouble();
        distribution = (Distribution) in.readValue(Distribution.class.getClassLoader());
        timeUnit = (TimeUnit) in.readValue(TimeUnit.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mean == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(mean);
        }
        if (deviance == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(deviance);
        }
        if (max == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(max);
        }
        dest.writeValue(distribution);
        dest.writeValue(timeUnit);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SimParams> CREATOR = new Parcelable.Creator<SimParams>() {
        @Override
        public SimParams createFromParcel(Parcel in) {
            return new SimParams(in);
        }

        @Override
        public SimParams[] newArray(int size) {
            return new SimParams[size];
        }
    };
    //endregion
}