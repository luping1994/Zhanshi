package net.suntrans.zhanshi.bean;

import java.util.List;

/**
 * Created by Looney on 2017/4/24.
 */

public class SceneEntity {
    public int code;
    public Result data;

    public static class Result {
        public int total;
        public List<Scene> lists;
    }

    public static class Scene  {
        public String id;
        public String name;
        public String sort;
        public String img_url;

    }
}
