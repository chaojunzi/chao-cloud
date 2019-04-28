package com.chao.cloud.common.support.map.tencent.address;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AddressResolveDTO {
    private int status;
    private String message;
    private ResultBean result;

    @Data
    public static class ResultBean {
        private String title;
        private LocationBean location;
        private Ad_infoBean ad_info;
        private Address_componentsBean address_components;
        private double similarity;
        private int deviation;
        private int reliability;
        private int level;

        @Data
        public static class LocationBean {
            private BigDecimal lng;
            private BigDecimal lat;

        }

        @Data
        public static class Ad_infoBean {
            private String adcode;
        }

        @Data
        public static class Address_componentsBean {
            private String province;
            private String city;
            private String district;
            private String street;
            private String street_number;

        }
    }
}