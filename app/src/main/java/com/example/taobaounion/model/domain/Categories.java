package com.example.taobaounion.model.domain;

import java.util.List;

public class Categories {

    /**
     * success : true
     * code : 10000
     * message : 获取分类成功.
     * data : [{"id":13366,"title":"推荐"},{"id":13375,"title":"食品"},{"id":13372,"title":"男装"},{"id":13367,"title":"女装"},{"id":13373,"title":"内衣"},{"id":13374,"title":"母婴"},{"id":13369,"title":"数码家电"},{"id":13371,"title":"美妆个护"},{"id":13376,"title":"运动户外"},{"id":13370,"title":"鞋包配饰"},{"id":13368,"title":"家居家装"}]
     */

    private boolean success;
    private int code;
    private String message;
    private List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 13366
         * title : 推荐
         */

        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Categories{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
