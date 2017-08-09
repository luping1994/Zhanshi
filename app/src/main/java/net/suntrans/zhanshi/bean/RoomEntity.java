package net.suntrans.zhanshi.bean;

import java.util.List;

/**
 * Created by Looney on 2017/7/23.
 */

public class RoomEntity {

    public int code;
    public Result data;

    public static class Result {
        public int total;
        public List<Room> lists;
    }

    public static class Room  {
        public String id;
        public String name;
        public String name_en;
        public String sort;
        public String img_url;

    }
}
