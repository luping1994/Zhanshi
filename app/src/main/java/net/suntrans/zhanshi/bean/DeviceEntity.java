package net.suntrans.zhanshi.bean;

import java.util.List;

/**
 * Created by Looney on 2017/6/10.
 */

public class DeviceEntity {

    public int code;
    public Data data;
    public String msg;
    public class Data {
        public int total;
        public List<ChannelInfo> lists;
    }

    public static class ChannelInfo {
        private String id;
        private String name;
        private String addr;
        private String vtype;
        private String img_url;
        private String device_type;
        private String xenon_addr;
        private String number;
        private String status;
        private String updated_at;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getVtype() {
            return vtype;
        }

        public void setVtype(String vtype) {
            this.vtype = vtype;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public String getXenon_addr() {
            return xenon_addr;
        }

        public void setXenon_addr(String xenon_addr) {
            this.xenon_addr = xenon_addr;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
    }
}
