package com.chao.cloud.admin.sys.controller;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.chao.cloud.admin.sys.constant.AdminConstant;
import com.chao.cloud.admin.sys.dal.entity.SysMenu;
import com.chao.cloud.admin.sys.domain.dto.MenuLayuiDTO;
import com.chao.cloud.admin.sys.log.AdminLog;
import com.chao.cloud.admin.sys.service.SysMenuService;
import com.chao.cloud.common.core.SpringContextUtil;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuAdmin;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuEnum;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuMapping;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间：2019年5月8日
 * @version 2.0
 */
@RequestMapping("/sys/menu")
@Controller
@Validated
@MenuMapping
public class MenuController extends BaseController {

	String prefix = "sys/menu";

	@Autowired
	private SysMenuService sysMenuService;

	@MenuMapping(value = "系统菜单", type = MenuEnum.MENU)
	@RequiresPermissions("sys:menu:list")
	@RequestMapping
	String list(Model model) {
		return prefix + "/menu";
	}

	@AdminLog(AdminLog.STAT_PREFIX + "菜单列表")
	@MenuMapping("列表")
	@RequiresPermissions("sys:menu:list")
	@RequestMapping("/list")
	@ResponseBody
	R<List<SysMenu>> list() {
		return R.ok(sysMenuService.list());
	}

	/**
	 * 菜单选择
	 * @return
	 */
	@Cacheable(cacheNames = "menu")
	@RequestMapping("/choose")
	@RequiresUser
	@ResponseBody
	public R<List<SysMenu>> choose() {
		// 获取当前角色的权限
		// UserDTO user = getUser();
		// Set<String> perms = getUser().getPerms();
		List<SysMenu> list = sysMenuService.list();
		// list.stream().filter(l ->
		// perms.contains(l.getPerms())).collect(Collectors.toList());
		return R.ok(list);
	}

	@AdminLog("添加菜单")
	@MenuMapping("增加")
	@RequiresPermissions("sys:menu:add")
	@RequestMapping("/add/{pId}")
	String add(Model model, @PathVariable("pId") Long pId) {
		model.addAttribute("pId", pId);
		if (pId == 0) {
			model.addAttribute("pName", "根目录");
		} else {
			model.addAttribute("pName", sysMenuService.getById(pId).getName());
		}
		return prefix + "/add";
	}

	@AdminLog("编辑菜单")
	@MenuMapping("编辑")
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping("/edit/{id}")
	String edit(Model model, @PathVariable("id") Integer id) {
		SysMenu mdo = sysMenuService.getById(id);
		Integer pId = mdo.getParentId();
		model.addAttribute("pId", pId);
		if (pId == 0) {
			model.addAttribute("pName", "根目录");
		} else {
			model.addAttribute("pName", sysMenuService.getById(pId).getName());
		}
		model.addAttribute("menu", mdo);
		return prefix + "/edit";
	}

	@AdminLog("保存菜单")
	@RequiresPermissions("sys:menu:add")
	@RequestMapping("/save")
	@ResponseBody
	Response<String> save(SysMenu menu) {
		DateTime date = DateUtil.date();
		menu.setGmtCreate(date);
		menu.setGmtModified(date);
		if (sysMenuService.save(menu)) {
			return Response.ok();
		}
		throw new BusinessException("保存失败");

	}

	@AdminLog("更新菜单")
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping("/update")
	@ResponseBody
	Response<String> update(SysMenu menu) {
		menu.setGmtModified(DateUtil.date());
		if (sysMenuService.updateById(menu)) {
			return Response.ok();
		}
		throw new BusinessException("更新失败");
	}

	@AdminLog("删除菜单")
	@MenuMapping("删除")
	@RequiresPermissions("sys:menu:remove")
	@RequestMapping("/remove")
	@ResponseBody
	Response<String> remove(@NotNull Integer id) {
		// 递归删除
		if (sysMenuService.recursionRemove(id)) {
			return Response.ok();
		}
		throw new BusinessException("删除失败");
	}

	/**
	 * 系统左侧导航菜单
	 * @return
	 */
	@RequestMapping("/leftList")
	@ResponseBody
	@RequiresUser
	List<MenuLayuiDTO> userList() {
		return sysMenuService.listMenuLayuiTree(getRoles());
	}

	/**
	 * --------------以下只有admin可以操作--------------------
	 */
	private final static List<MenuAdmin> MENU_ADMIN = CollUtil.newArrayList();

	@RequiresRoles(AdminConstant.ADMIN)
	@RequestMapping("admin")
	String admin() {
		return prefix + "/admin";
	}

	@RequiresRoles(AdminConstant.ADMIN)
	@RequestMapping("admin/list")
	@ResponseBody
	R<List<MenuAdmin>> adminList() {
		if (MENU_ADMIN.size() < 1) {
			this.initMenuAdmin();
		}
		// 获取已有的url
		List<SysMenu> allMenu = sysMenuService.list();
		Set<String> existUrl = allMenu.stream().map(SysMenu::getUrl).collect(Collectors.toSet());
		// 排除 已经存在 的路径
		List<MenuAdmin> result = MENU_ADMIN.stream().filter(m -> !existUrl.contains(m.getUrl()))
				.collect(Collectors.toList());
		return R.ok(result);
	}

	@RequiresRoles(AdminConstant.ADMIN)
	@GetMapping("adminAdd/{menuId}")
	String adminAdd(@PathVariable("menuId") Long menuId, Model model) {
		// 获取一级菜单
		List<SysMenu> dir = sysMenuService
				.list(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getType, MenuEnum.DIR.type));
		model.addAttribute("dir", dir);
		// 获取需要展示的菜单
		List<MenuAdmin> menus = MENU_ADMIN.stream()
				.filter(d -> menuId.equals(d.getMenuId()) || menuId.equals(d.getParentId()))
				.collect(Collectors.toList());
		if (CollUtil.isEmpty(menus)) {
			throw new BusinessException("无效的菜单id:" + menuId);
		}
		model.addAttribute("menus", menus);
		model.addAttribute("menuId", menuId);
		return prefix + "/adminAdd";
	}

	@RequiresRoles(AdminConstant.ADMIN)
	@PostMapping("adminAdd")
	@ResponseBody
	Response<String> adminAdd(@NotNull Long menuId, @NotNull Long parentId) {
		// 获取需要展示的菜单
		List<MenuAdmin> menus = MENU_ADMIN.stream()
				.filter(d -> menuId.equals(d.getMenuId()) || menuId.equals(d.getParentId()))
				.collect(Collectors.toList());
		if (CollUtil.isEmpty(menus)) {
			throw new BusinessException("无效的菜单id:" + menuId);
		}
		MenuAdmin root = menus.stream().filter(m -> menuId.equals(m.getMenuId())).findFirst().orElse(null);
		if (BeanUtil.isEmpty(root)) {
			throw new BusinessException("无效的菜单id:" + menuId);
		}
		// 判断菜单是否已经存在
		int count = sysMenuService.count(Wrappers.<SysMenu>lambdaQuery().eq(SysMenu::getUrl, root.getUrl()));
		if (count > 0) {
			throw new BusinessException(StrUtil.format("[{}] 已存在，请删除后重试", root.getName()));
		}
		// 批量添加
		menus.remove(root);
		root.setParentId(parentId);
		boolean result = sysMenuService.adminSaveBatch(root, menus);
		if (!result) {
			throw new BusinessException("生成菜单失败:" + root.getName());
		}
		return Response.ok();
	}

	public void initMenuAdmin() {
		Map<String, Object> map = SpringContextUtil.getApplicationContext().getBeansWithAnnotation(MenuMapping.class);
		if (MapUtil.isNotEmpty(map)) {
			// 循环
			Long menuId = 1L;
			for (Object value : map.values()) {
				Long parentId = 0L;
				Class<Object> clazz = ClassUtil.loadClass(value.toString().split("@")[0]);
				RequestMapping url = clazz.getAnnotation(RequestMapping.class);
				Method[] methods = ReflectUtil.getMethods(clazz, new Filter<Method>() {
					@Override
					public boolean accept(Method t) {
						return t.isAnnotationPresent(MenuMapping.class)
								&& t.isAnnotationPresent(RequiresPermissions.class);
					}
				});
				List<Method> menus = CollUtil.toList(methods);
				String mapping = CollUtil.toList(url.value()).stream().collect(Collectors.joining(","));
				// 获取管理菜单
				Method root = menus.stream().filter(m -> m.getAnnotation(MenuMapping.class).type() == MenuEnum.MENU)
						.findFirst().orElse(null);
				if (root != null) {
					menuId++;
					parentId = menuId;
					MenuAdmin menu = this.genAdminMenu(root, menuId, 0L, mapping);
					MENU_ADMIN.add(menu);
				}
				// 删除root
				menus.remove(root);
				for (Method m : menus) {
					menuId++;
					MenuAdmin menu = genAdminMenu(m, menuId, parentId, mapping);
					MENU_ADMIN.add(menu);
				}
			}
		}
	}

	private MenuAdmin genAdminMenu(Method m, Long menuId, Long parentId, String mapping) {
		MenuMapping menu = m.getAnnotation(MenuMapping.class);
		RequiresPermissions permissions = m.getAnnotation(RequiresPermissions.class);
		RequestMapping rm = m.getAnnotation(RequestMapping.class);
		MenuAdmin menuAdmin = new MenuAdmin();
		menuAdmin.setMenuId(menuId);
		menuAdmin.setName(menu.value());
		menuAdmin.setParentId(parentId);
		menuAdmin.setPerms(ArrayUtil.join(permissions.value(), ","));
		menuAdmin.setType(menu.type().type);
		menuAdmin.setUrl(mapping + ArrayUtil.join(rm.value(), ","));
		return menuAdmin;
	}
}
