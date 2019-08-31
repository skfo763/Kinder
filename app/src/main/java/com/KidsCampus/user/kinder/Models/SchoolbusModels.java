package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class SchoolbusModels {

    public Map<String, schoolbus> schoolbusinfo = new HashMap<>();

    public static class schoolbus implements Parcelable {
         public String officeedu;
         public String subofficeedu;
         public String kindername;
         public String establish;
         public String vhcl_oprn_yn;
         public String opra_vhcnt;
         public String dclr_vhcnt;
         public String psg9_dclr_vhcnt;
         public String psg12_dclr_vhcnt;
         public String psg15_dclr_vhcnt;

        public schoolbus(Parcel in) {
            officeedu = in.readString();
            subofficeedu = in.readString();
            kindername = in.readString();
            establish = in.readString();
            vhcl_oprn_yn = in.readString();
            opra_vhcnt = in.readString();
            dclr_vhcnt = in.readString();
            psg9_dclr_vhcnt = in.readString();
            psg12_dclr_vhcnt = in.readString();
            psg15_dclr_vhcnt = in.readString();
        }

        public static final Creator<schoolbus> CREATOR = new Creator<schoolbus>() {
            @Override
            public schoolbus createFromParcel(Parcel in) {
                return new schoolbus(in);
            }

            @Override
            public schoolbus[] newArray(int size) {
                return new schoolbus[size];
            }
        };

        public schoolbus() {

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
            dest.writeString(vhcl_oprn_yn);
            dest.writeString(opra_vhcnt);
            dest.writeString(dclr_vhcnt);
            dest.writeString(psg9_dclr_vhcnt);
            dest.writeString(psg12_dclr_vhcnt);
            dest.writeString(psg15_dclr_vhcnt);
        }
    }
}
