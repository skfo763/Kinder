package com.KidsCampus.user.kinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class MealModels {

    public Map<String, meals> mealinfo = new HashMap<>();

    public static class meals implements Parcelable{
        public String officeedu;
        public String subofficeedu;
        public String kindername;
        public String establish;
        public String mlsr_oprn_way_tp_cd;
        public String cons_ents_nm;
        public String al_kpcnt;
        public String mlsr_kpcnt;
        public String ntrt_tchr_agmt_yn;
        public String snge_agmt_ntrt_thcnt;
        public String cprt_agmt_ntrt_thcnt;
        public String ckcnt;
        public String cmcnt;
        public String mas_mspl_dclr_yn;

        public meals(Parcel in) {
            officeedu = in.readString();
            subofficeedu = in.readString();
            kindername = in.readString();
            establish = in.readString();
            mlsr_oprn_way_tp_cd = in.readString();
            cons_ents_nm = in.readString();
            al_kpcnt = in.readString();
            mlsr_kpcnt = in.readString();
            ntrt_tchr_agmt_yn = in.readString();
            snge_agmt_ntrt_thcnt = in.readString();
            cprt_agmt_ntrt_thcnt = in.readString();
            ckcnt = in.readString();
            cmcnt = in.readString();
            mas_mspl_dclr_yn = in.readString();
        }

        public static final Creator<meals> CREATOR = new Creator<meals>() {
            @Override
            public meals createFromParcel(Parcel in) {
                return new meals(in);
            }

            @Override
            public meals[] newArray(int size) {
                return new meals[size];
            }
        };

        public meals() {

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
            dest.writeString(mlsr_oprn_way_tp_cd);
            dest.writeString(cons_ents_nm);
            dest.writeString(al_kpcnt);
            dest.writeString(mlsr_kpcnt);
            dest.writeString(ntrt_tchr_agmt_yn);
            dest.writeString(snge_agmt_ntrt_thcnt);
            dest.writeString(cprt_agmt_ntrt_thcnt);
            dest.writeString(ckcnt);
            dest.writeString(cmcnt);
            dest.writeString(mas_mspl_dclr_yn);
        }
    }
}
