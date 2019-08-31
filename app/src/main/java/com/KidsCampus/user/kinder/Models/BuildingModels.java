package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class BuildingModels {

    public Map<String, building> buildingInfo = new HashMap<>();

    public static class building implements Parcelable {
        public String officeedu;
        public String subofficeedu;
        public String kindername;
        public String establish;
        public String archyy;
        public String floorcnt;
        public String bldgprusarea;
        public String grottar;

        public building(Parcel in) {
            officeedu = in.readString();
            subofficeedu = in.readString();
            kindername = in.readString();
            establish = in.readString();
            archyy = in.readString();
            floorcnt = in.readString();
            bldgprusarea = in.readString();
            grottar = in.readString();
        }

        public static final Creator<building> CREATOR = new Creator<building>() {
            @Override
            public building createFromParcel(Parcel in) {
                return new building(in);
            }

            @Override
            public building[] newArray(int size) {
                return new building[size];
            }
        };

        public building() { }

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
            dest.writeString(archyy);
            dest.writeString(floorcnt);
            dest.writeString(bldgprusarea);
            dest.writeString(grottar);
        }
    }
}
