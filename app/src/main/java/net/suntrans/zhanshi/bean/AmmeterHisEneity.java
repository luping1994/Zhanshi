package net.suntrans.zhanshi.bean;

import java.util.List;

/**
 * Created by Looney on 2017/7/13.
 */

public class AmmeterHisEneity {
    public int code;
    public Data data;
    public  static class Data{
        public int total;
        public String unit;
        public List<HisItem> lists;
    }

    public static class HisItem{
        public String data;
        public String created_at;
    }
}
