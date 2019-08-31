package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class HygeneModels {

    public Map<String, hygene> hygeneinfo = new HashMap<>();

    public static class hygene implements Parcelable{
        public String  officeedu;
        public String  subofficeedu;
        public String  kindername;
        public String  estb_pt;
        public String  arql_chk_dt;
        public String  arql_chk_rslt_tp_cd;
        public String  fxtm_dsnf_trgt_yn;
        public String  fxtm_dsnf_chk_dt;
        public String  fxtm_dsnf_chk_rslt_tp_cd;
        public String  tp_01;
        public String  tp_02;
        public String  tp_03;
        public String  tp_04;
        public String  unwt_qlwt_insc_yn;
        public String  qlwt_insc_dt;
        public String  qlwt_insc_stby_yn;
        public String  mdst_chk_dt;
        public String  mdst_chk_rslt_cd;
        public String  ilmn_chk_dt;
        public String  ilmn_chk_rslt_cd;

        public hygene(Parcel in) {
            officeedu = in.readString();
            subofficeedu = in.readString();
            kindername = in.readString();
            estb_pt = in.readString();
            arql_chk_dt = in.readString();
            arql_chk_rslt_tp_cd = in.readString();
            fxtm_dsnf_trgt_yn = in.readString();
            fxtm_dsnf_chk_dt = in.readString();
            fxtm_dsnf_chk_rslt_tp_cd = in.readString();
            tp_01 = in.readString();
            tp_02 = in.readString();
            tp_03 = in.readString();
            tp_04 = in.readString();
            unwt_qlwt_insc_yn = in.readString();
            qlwt_insc_dt = in.readString();
            qlwt_insc_stby_yn = in.readString();
            mdst_chk_dt = in.readString();
            mdst_chk_rslt_cd = in.readString();
            ilmn_chk_dt = in.readString();
            ilmn_chk_rslt_cd = in.readString();
        }

        public static final Creator<hygene> CREATOR = new Creator<hygene>() {
            @Override
            public hygene createFromParcel(Parcel in) {
                return new hygene(in);
            }

            @Override
            public hygene[] newArray(int size) {
                return new hygene[size];
            }
        };

        public hygene() {

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
            dest.writeString(arql_chk_dt);
            dest.writeString(arql_chk_rslt_tp_cd);
            dest.writeString(fxtm_dsnf_trgt_yn);
            dest.writeString(fxtm_dsnf_chk_dt);
            dest.writeString(fxtm_dsnf_chk_rslt_tp_cd);
            dest.writeString(tp_01);
            dest.writeString(tp_02);
            dest.writeString(tp_03);
            dest.writeString(tp_04);
            dest.writeString(unwt_qlwt_insc_yn);
            dest.writeString(qlwt_insc_dt);
            dest.writeString(qlwt_insc_stby_yn);
            dest.writeString(mdst_chk_dt);
            dest.writeString(mdst_chk_rslt_cd);
            dest.writeString(ilmn_chk_dt);
            dest.writeString(ilmn_chk_rslt_cd);
        }
    }
}
