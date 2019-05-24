package com.chao.cloud.admin.system.domain.vo;

import javax.validation.constraints.NotBlank;

import lombok.Data;

public interface GenCodeVO {

    @Data
    public static class Base {
        @NotBlank(message = "不能为空")
        private String url;
        @NotBlank(message = "不能为空")
        private String username;
        @NotBlank(message = "不能为空")
        private String password;
        @NotBlank(message = "不能为空")
        private String driverName;
        @NotBlank(message = "不能为空")
        private String parent;// package
        @NotBlank(message = "不能为空")
        private String author;// 作者
    }
}
