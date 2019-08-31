package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class SafetyModels {

    public Map<String, safety> safetyinfo = new HashMap<>();

    public static class safety implements Parcelable {
         public String officeedu;
         public String subofficeedu;
         public String kindername;
         public String estb_pt;
         public String fire_avd_yn;
         public String fire_avd_dt;
         public String gas_ck_yn;
         public String gas_ck_dt;
         public String fire_safe_yn;
         public String fire_safe_dt;
         public String elect_ck_yn;
         public String elect_ck_dt;
         public String plyg_ck_yn;
         public String plyg_ck_dt;
         public String plyg_ck_rs_cd;
         public String cctv_ist_yn;
         public String cctv_ist_total;
         public String cctv_ist_in;
         public String cctv_ist_out;

         public safety(Parcel in) {
              officeedu = in.readString();
              subofficeedu = in.readString();
              kindername = in.readString();
              estb_pt = in.readString();
              fire_avd_yn = in.readString();
              fire_avd_dt = in.readString();
              gas_ck_yn = in.readString();
              gas_ck_dt = in.readString();
              fire_safe_yn = in.readString();
              fire_safe_dt = in.readString();
              elect_ck_yn = in.readString();
              elect_ck_dt = in.readString();
              plyg_ck_yn = in.readString();
              plyg_ck_dt = in.readString();
              plyg_ck_rs_cd = in.readString();
              cctv_ist_yn = in.readString();
              cctv_ist_total = in.readString();
              cctv_ist_in = in.readString();
              cctv_ist_out = in.readString();
         }

         public static final Creator<safety> CREATOR = new Creator<safety>() {
              @Override
              public safety createFromParcel(Parcel in) {
                   return new safety(in);
              }

              @Override
              public safety[] newArray(int size) {
                   return new safety[size];
              }
         };

        public safety() {

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
              dest.writeString(fire_avd_yn);
              dest.writeString(fire_avd_dt);
              dest.writeString(gas_ck_yn);
              dest.writeString(gas_ck_dt);
              dest.writeString(fire_safe_yn);
              dest.writeString(fire_safe_dt);
              dest.writeString(elect_ck_yn);
              dest.writeString(elect_ck_dt);
              dest.writeString(plyg_ck_yn);
              dest.writeString(plyg_ck_dt);
              dest.writeString(plyg_ck_rs_cd);
              dest.writeString(cctv_ist_yn);
              dest.writeString(cctv_ist_total);
              dest.writeString(cctv_ist_in);
              dest.writeString(cctv_ist_out);
         }
    }
}
