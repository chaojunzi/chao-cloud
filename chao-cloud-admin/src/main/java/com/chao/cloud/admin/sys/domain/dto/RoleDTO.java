package com.chao.cloud.admin.sys.domain.dto;

import java.util.List;

import lombok.Data;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Data
public class RoleDTO {

	private Integer roleId;
	private String roleName;
	private String roleSign;
	private String rights;
	private String remark;
	private List<Integer> menuIds;

}
