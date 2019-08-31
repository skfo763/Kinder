package com.KidsCampus.user.kinder.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KinderBoardModels {

    public Map<String, kinderboards> kinderinfo = new HashMap<>();

    public static class kinderboards {
        public String title;
        public String date;
        public Double star;
        public String context;
        public String cir;
        public String tec;
        public String pay;
        public String payrate;
        public String safety;
    }
}
