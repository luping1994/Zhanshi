package net.suntrans.zhanshi.bean;

import java.util.List;

/**
 * Created by Looney on 2017/7/23.
 */

public class AmeterEntity {

    public int code;
    public Result data;

    public static class Result {
        public int total;
        public List<Ameter> lists;
    }

    public static class Ameter  {
        public String id;
        public String dev_id;
        public String sno;
        public String vtype;
        public String title;

    }
}
