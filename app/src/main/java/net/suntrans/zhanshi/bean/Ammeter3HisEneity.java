package net.suntrans.zhanshi.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by Looney on 2017/7/13.
 */

public class Ammeter3HisEneity {
    public int code;
    public Data data;
    public  static class Data{
        public int total;
        public String unit;
        public List<Map<String,String>> lists;
    }

}
