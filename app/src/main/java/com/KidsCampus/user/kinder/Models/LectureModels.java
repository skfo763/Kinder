package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class LectureModels {

    public Map<String, lectures> lectureinfo = new HashMap<>();

    public static class lectures implements Parcelable {
         public String officeedu;
         public String subofficeedu;
         public String kindername;
         public String establish;
         public String ag3_lsn_dcnt;
         public String ag4_lsn_dcnt;
         public String ag5_lsn_dcnt;
         public String mix_age_lsn_dcnt;
         public String spcl_lsn_dcnt;
         public String afsc_pros_lsn_dcnt;
         public String ldnum_blw_yn;
         public String fdtn_kndr_yn;

        public lectures(Parcel in) {
            officeedu = in.readString();
            subofficeedu = in.readString();
            kindername = in.readString();
            establish = in.readString();
            ag3_lsn_dcnt = in.readString();
            ag4_lsn_dcnt = in.readString();
            ag5_lsn_dcnt = in.readString();
            mix_age_lsn_dcnt = in.readString();
            spcl_lsn_dcnt = in.readString();
            afsc_pros_lsn_dcnt = in.readString();
            ldnum_blw_yn = in.readString();
            fdtn_kndr_yn = in.readString();
        }

        public static final Creator<lectures> CREATOR = new Creator<lectures>() {
            @Override
            public lectures createFromParcel(Parcel in) {
                return new lectures(in);
            }

            @Override
            public lectures[] newArray(int size) {
                return new lectures[size];
            }
        };

        public lectures() {

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
            dest.writeString(ag3_lsn_dcnt);
            dest.writeString(ag4_lsn_dcnt);
            dest.writeString(ag5_lsn_dcnt);
            dest.writeString(mix_age_lsn_dcnt);
            dest.writeString(spcl_lsn_dcnt);
            dest.writeString(afsc_pros_lsn_dcnt);
            dest.writeString(ldnum_blw_yn);
            dest.writeString(fdtn_kndr_yn);
        }
    }
}
