package com.example.taobaounion.model.domain;

public class TicketResult {

    /**
     * success : true
     * code : 10000
     * message : 淘宝口令构建成功!
     * data : {"tbk_tpwd_create_response":{"data":{"model":"88￥ CZ0001 qcTad8cLUSG￥ https://m.tb.cn/h.UpyusKa  【专柜同款】第二第三件0元！Missface补水面膜眼霜精华自选套装"},"request_id":"16lsrd79sa221"}}
     */

    private boolean success;
    private int code;
    private String message;
    private DataBeanX data;

    @Override
    public String toString() {
        return "TicketResult{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

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

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {
        @Override
        public String toString() {
            return "DataBeanX{" +
                    "tbk_tpwd_create_response=" + tbk_tpwd_create_response +
                    '}';
        }

        /**
         * tbk_tpwd_create_response : {"data":{"model":"88￥ CZ0001 qcTad8cLUSG￥ https://m.tb.cn/h.UpyusKa  【专柜同款】第二第三件0元！Missface补水面膜眼霜精华自选套装"},"request_id":"16lsrd79sa221"}
         */

        private TbkTpwdCreateResponseBean tbk_tpwd_create_response;

        public TbkTpwdCreateResponseBean getTbk_tpwd_create_response() {
            return tbk_tpwd_create_response;
        }

        public void setTbk_tpwd_create_response(TbkTpwdCreateResponseBean tbk_tpwd_create_response) {
            this.tbk_tpwd_create_response = tbk_tpwd_create_response;
        }

        public static class TbkTpwdCreateResponseBean {
            @Override
            public String toString() {
                return "TbkTpwdCreateResponseBean{" +
                        "data=" + data +
                        ", request_id='" + request_id + '\'' +
                        '}';
            }

            /**
             * data : {"model":"88￥ CZ0001 qcTad8cLUSG￥ https://m.tb.cn/h.UpyusKa  【专柜同款】第二第三件0元！Missface补水面膜眼霜精华自选套装"}
             * request_id : 16lsrd79sa221
             */

            private DataBean data;
            private String request_id;

            public DataBean getData() {
                return data;
            }

            public void setData(DataBean data) {
                this.data = data;
            }

            public String getRequest_id() {
                return request_id;
            }

            public void setRequest_id(String request_id) {
                this.request_id = request_id;
            }

            public static class DataBean {
                @Override
                public String toString() {
                    return "DataBean{" +
                            "model='" + model + '\'' +
                            '}';
                }

                /**
                 * model : 88￥ CZ0001 qcTad8cLUSG￥ https://m.tb.cn/h.UpyusKa  【专柜同款】第二第三件0元！Missface补水面膜眼霜精华自选套装
                 */

                private String model;

                public String getModel() {
                    return model;
                }

                public void setModel(String model) {
                    this.model = model;
                }
            }
        }
    }
}
