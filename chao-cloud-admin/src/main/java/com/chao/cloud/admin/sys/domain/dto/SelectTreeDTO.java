package com.chao.cloud.admin.sys.domain.dto;

import java.util.List;

import lombok.Data;

@Data
public class SelectTreeDTO {

	private Integer id;
	private Integer pid;
	private String name;
	private Integer value;
	private boolean disabled = false;
	private List<SelectTreeDTO> children;

}
