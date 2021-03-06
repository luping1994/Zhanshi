package net.suntrans.zhanshi.bean;

import java.util.List;

/**
 * Created by Looney on 2017/7/8.
 */

public class AreaEntity {
    public int code;
    public AreaData data;
    public String msg;
    public static class AreaData{
        public int total;
        public List<AreaFloor> lists;

    }

    public static class AreaFloor{
        public int id;
        public String user_id;
        public String name;
        public String img_url;
        public String show_sort;
        public List<FloorRoom> sub;
    }

    public static class FloorRoom{
        public int id;
        public String house_id;
        public String name;
        public String img_url;
        public String show_sort;

    }
}
