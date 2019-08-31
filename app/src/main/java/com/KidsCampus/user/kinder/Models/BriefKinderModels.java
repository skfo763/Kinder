package com.KidsCampus.user.kinder.Models;

import java.util.HashMap;
import java.util.Map;

public class BriefKinderModels {

    public Map<String, kinder> Bkinderinfp = new HashMap<>();

    public static class kinder {
        public String sidoCode;
        public String sggCode;
        public String sidoname;
        public String sggname;
        public String kindername;
        public String addr;
        public String telno;
        public String establish;
    }
}
