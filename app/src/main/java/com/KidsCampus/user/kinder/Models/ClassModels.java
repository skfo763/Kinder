package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class ClassModels {

    public Map<String, classmod> classinfo = new HashMap<>();

    public static class classmod implements Parcelable{
        public String officeedu;
        public String subofficeedu;
        public String kindername;
        public String establish;
        public String crcnt;
        public String clsrarea;
        public String phgrindrarea;
        public String hlsparea;
        public String ktchmssparea;
        public String otsparea;

        public classmod(Parcel in) {
            officeedu = in.readString();
            subofficeedu = in.readString();
            kindername = in.readString();
            establish = in.readString();
            crcnt = in.readString();
            clsrarea = in.readString();
            phgrindrarea = in.readString();
            hlsparea = in.readString();
            ktchmssparea = in.readString();
            otsparea = in.readString();
        }

        public static final Creator<classmod> CREATOR = new Creator<classmod>() {
            @Override
            public classmod createFromParcel(Parcel in) {
                return new classmod(in);
            }

            @Override
            public classmod[] newArray(int size) {
                return new classmod[size];
            }
        };

        public classmod() {

        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(officeedu);
            dest.writeString(subofficeedu);
            dest.writeString(kindername);
            dest.writeString(establish);
            dest.writeString(crcnt);
            dest.writeString(clsrarea);
            dest.writeString(phgrindrarea);
            dest.writeString(hlsparea);
            dest.writeString(ktchmssparea);
            dest.writeString(otsparea);
        }
    }
}
