package com.fs.covid_19.item;

import android.os.Parcel;
import android.os.Parcelable;

public class Rekam implements Parcelable {
    String name,macAddress,date;

    public Rekam() {
    }

    protected Rekam(Parcel in) {
        name = in.readString();
        macAddress = in.readString();
        date = in.readString();
    }

    public static final Creator<Rekam> CREATOR = new Creator<Rekam>() {
        @Override
        public Rekam createFromParcel(Parcel in) {
            return new Rekam(in);
        }

        @Override
        public Rekam[] newArray(int size) {
            return new Rekam[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(macAddress);
        parcel.writeString(date);
    }
}
