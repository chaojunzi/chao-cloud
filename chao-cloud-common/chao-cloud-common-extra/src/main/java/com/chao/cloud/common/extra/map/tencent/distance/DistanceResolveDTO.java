package com.chao.cloud.common.extra.map.tencent.distance;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class DistanceResolveDTO {
    private int status;
    private String message;
    private ResultBean result;

    @Data
    public static class ResultBean {
        private List<ElementsBean> elements;

        @Data
        public static class ElementsBean {
            private FromBean from;
            private ToBean to;
            private int distance;
            private int duration;

            @Data
            public static class FromBean {
                private BigDecimal lat;
                private BigDecimal lng;

            }

            @Data
            public static class ToBean {
                private BigDecimal lat;
                private BigDecimal lng;

            }
        }
    }
}