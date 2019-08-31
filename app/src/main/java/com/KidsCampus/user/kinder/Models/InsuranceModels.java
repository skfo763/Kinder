package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class InsuranceModels {

    public Map<String, insurance> insuranceinfo = new HashMap<>();

    public static class insurance implements Parcelable{
          public String officeedu;
         public String subofficeedu;
         public String kindername;
         public String estb_pt;
         public String insurance_nm;
         public String insurance_en;
         public String insurance_yn;
         public String company1;
         public String company2;
         public String company3;

        public insurance(Parcel in) {
            officeedu = in.readString();
            subofficeedu = in.readString();
            kindername = in.readString();
            estb_pt = in.readString();
            insurance_nm = in.readString();
            insurance_en = in.readString();
            insurance_yn = in.readString();
            company1 = in.readString();
            company2 = in.readString();
            company3 = in.readString();
        }

        public static final Creator<insurance> CREATOR = new Creator<insurance>() {
            @Override
            public insurance createFromParcel(Parcel in) {
                return new insurance(in);
            }

            @Override
            public insurance[] newArray(int size) {
                return new insurance[size];
            }
        };

        public insurance() {

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
            dest.writeString(insurance_nm);
            dest.writeString(insurance_en);
            dest.writeString(insurance_yn);
            dest.writeString(company1);
            dest.writeString(company2);
            dest.writeString(company3);
        }
    }
}
