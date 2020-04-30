package com.fs.covid_19.item;

import android.os.Parcel;
import android.os.Parcelable;

public class Member implements Parcelable {
    private String Name,Email,PhoneNumber,Password,MacAddress;

    public Member() {
    }

    protected Member(Parcel in) {
        Name = in.readString();
        Email = in.readString();
        PhoneNumber = in.readString();
        Password = in.readString();
        MacAddress = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String macAddress) {
        MacAddress = macAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Name);
        parcel.writeString(Email);
        parcel.writeString(PhoneNumber);
        parcel.writeString(Password);
        parcel.writeString(MacAddress);
    }
}
