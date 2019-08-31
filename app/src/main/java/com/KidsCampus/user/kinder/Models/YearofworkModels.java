package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class YearofworkModels {

    public Map<String, yearofwork> yearofworkinfo = new HashMap<>();

    public static class yearofwork implements Parcelable {
         public String officeedu;
         public String subofficeedu;
         public String kindername;
         public String yy1_undr_thcnt;
         public String yy1_abv_yy2_undr_thcnt;
         public String yy2_abv_yy4_undr_thcnt;
         public String yy4_abv_yy6_undr_thcnt;
         public String  yy6_abv_thcnt;

        public yearofwork(Parcel in) {
            officeedu = in.readString();
            subofficeedu = in.readString();
            kindername = in.readString();
            yy1_undr_thcnt = in.readString();
            yy1_abv_yy2_undr_thcnt = in.readString();
            yy2_abv_yy4_undr_thcnt = in.readString();
            yy4_abv_yy6_undr_thcnt = in.readString();
            yy6_abv_thcnt = in.readString();
        }

        public static final Creator<yearofwork> CREATOR = new Creator<yearofwork>() {
            @Override
            public yearofwork createFromParcel(Parcel in) {
                return new yearofwork(in);
            }

            @Override
            public yearofwork[] newArray(int size) {
                return new yearofwork[size];
            }
        };

        public yearofwork() {

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
            dest.writeString(yy1_undr_thcnt);
            dest.writeString(yy1_abv_yy2_undr_thcnt);
            dest.writeString(yy2_abv_yy4_undr_thcnt);
            dest.writeString(yy4_abv_yy6_undr_thcnt);
            dest.writeString(yy6_abv_thcnt);
        }
    }
}
