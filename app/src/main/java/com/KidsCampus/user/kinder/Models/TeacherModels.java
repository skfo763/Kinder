package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class TeacherModels {

    public Map<String, teachers> teacherinfo = new HashMap<>();

    public static class teachers implements Parcelable{
        public String officeedu;
        public String subofficeedu;
        public String kindername;
        public String establish;
        public String drcnt;
        public String adcnt;
        public String hdst_thcnt;
        public String asps_thcnt;
        public String gnrl_thcnt;
        public String spcn_thcnt;
        public String ntcnt;
        public String ntrt_thcnt;
        public String shcnt_thcnt;
        public String incnt;
        public String owcnt;
        public String hdst_tchr_qacnt;
        public String rgth_gd1_qacnt;
        public String rgth_gd2_qacnt;
        public String asth_qacnt;

        public teachers(Parcel in) {
            officeedu = in.readString();
            subofficeedu = in.readString();
            kindername = in.readString();
            establish = in.readString();
            drcnt = in.readString();
            adcnt = in.readString();
            hdst_thcnt = in.readString();
            asps_thcnt = in.readString();
            gnrl_thcnt = in.readString();
            spcn_thcnt = in.readString();
            ntcnt = in.readString();
            ntrt_thcnt = in.readString();
            shcnt_thcnt = in.readString();
            incnt = in.readString();
            owcnt = in.readString();
            hdst_tchr_qacnt = in.readString();
            rgth_gd1_qacnt = in.readString();
            rgth_gd2_qacnt = in.readString();
            asth_qacnt = in.readString();
        }

        public static final Creator<teachers> CREATOR = new Creator<teachers>() {
            @Override
            public teachers createFromParcel(Parcel in) {
                return new teachers(in);
            }

            @Override
            public teachers[] newArray(int size) {
                return new teachers[size];
            }
        };

        public teachers() {

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
            dest.writeString(drcnt);
            dest.writeString(adcnt);
            dest.writeString(hdst_thcnt);
            dest.writeString(asps_thcnt);
            dest.writeString(gnrl_thcnt);
            dest.writeString(spcn_thcnt);
            dest.writeString(ntcnt);
            dest.writeString(ntrt_thcnt);
            dest.writeString(shcnt_thcnt);
            dest.writeString(incnt);
            dest.writeString(owcnt);
            dest.writeString(hdst_tchr_qacnt);
            dest.writeString(rgth_gd1_qacnt);
            dest.writeString(rgth_gd2_qacnt);
            dest.writeString(asth_qacnt);
        }
    }
}
