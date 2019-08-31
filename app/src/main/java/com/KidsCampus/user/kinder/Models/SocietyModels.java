package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class SocietyModels {

    public Map<String, societies> societyinfo = new HashMap<>();

    public static class societies implements Parcelable {
         public String officeedu; 
         public String subofficeedu; 
         public String kindername; 
         public String estb_pt; 
         public String school_ds_yn;   
         public String school_ds_en;
         public String educate_ds_yn;  
         public String ducate_ds_en;

        public societies(Parcel in) {
            officeedu = in.readString();
            subofficeedu = in.readString();
            kindername = in.readString();
            estb_pt = in.readString();
            school_ds_yn = in.readString();
            school_ds_en = in.readString();
            educate_ds_yn = in.readString();
            ducate_ds_en = in.readString();
        }

        public static final Creator<societies> CREATOR = new Creator<societies>() {
            @Override
            public societies createFromParcel(Parcel in) {
                return new societies(in);
            }

            @Override
            public societies[] newArray(int size) {
                return new societies[size];
            }
        };

        public societies() {

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
            dest.writeString(estb_pt);
            dest.writeString(school_ds_yn);
            dest.writeString(school_ds_en);
            dest.writeString(educate_ds_yn);
            dest.writeString(ducate_ds_en);
        }
    }
}
