package com.qianfeng.application.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.qianfeng.application.model.Menu;
import com.qianfeng.application.model.Menus;
import com.qianfeng.application.model.User;
import com.qianfeng.application.service.IMenuService;
import com.qianfeng.application.service.IUserService;
import com.qianfeng.common.controller.BaseController;

@Controller
@RequestMapping("/menu")
public class MenuController extends BaseController {

	@Resource(name = "menuServiceImpl")
	private IMenuService menuService;
	
	@Autowired
	private IUserService userSerivce;
	
	@RequestMapping("/menuMana")
	public ModelAndView toMenuMana(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView result = new ModelAndView("menu/menuMana");
		return result;
	}

	// 分页显示菜单信息
	@SuppressWarnings("unchecked")
	@RequestMapping("/menuPage")
	public ModelAndView menuPage(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> paramMap = this.getParam(request);
		Map<String, Object> retMap = menuService.queryMenuPage(paramMap);

		List<Menu> menuList = (List<Menu>) retMap.get("menuList");
		long total = Long.parseLong(retMap.get("total").toString());

		ModelAndView result = new ModelAndView("menu/menuList");
		result.addObject("menuList", menuList);
		result.addObject("total", total);

		return result;
	}

	// 展示页码信息
	@RequestMapping("/getPageNumber")
	public ModelAndView getPageNumber(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView result = new ModelAndView("menu/menuPageNumber");
		int total = Integer.parseInt(request.getParameter("total"));
		int startIndex = Integer.parseInt(request.getParameter("startIndex"));
		int pageSize = Integer.parseInt(request.getParameter("pageSize"));
		result = this.getPageNumberInfo(total, startIndex, pageSize, result);
		return result;
	}

	// 添加组织信息
	@RequestMapping("/add")
	@ResponseBody
	public void insert(HttpServletRequest request, HttpServletResponse response, Menu menu) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			menuService.insert(menu);
			retMap.put("isSucess", true);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("isSucess", false);
		}

		Gson gson = new Gson();
		String resultJson = gson.toJson(retMap);
		this.flushResponse(response, resultJson);
	}

	// 查看组织信息
	@RequestMapping("/queryMenu")
	@ResponseBody
	public void selectByPrimaryKey(HttpServletRequest request, HttpServletResponse response, long menuId) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			// 此对象继承了Menu对象，多了一个menuParentName字段
			Menus menu = menuService.selectMenusByPrimaryKey(menuId);
			retMap.put("menu", menu);
			retMap.put("isSucess", true);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("isSucess", false);
		}

		Gson gson = new Gson();
		String resultJson = gson.toJson(retMap);
		this.flushResponse(response, resultJson);
	}

	// 修改组织信息
	@RequestMapping("/update")
	@ResponseBody
	public void updateByPrimaryKeySelective(HttpServletRequest request, HttpServletResponse response, Menu menu) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			menuService.updateByPrimaryKeySelective(menu);
			retMap.put("isSucess", true);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("isSucess", false);
		}

		Gson gson = new Gson();
		String resultJson = gson.toJson(retMap);
		this.flushResponse(response, resultJson);
	}

	// 删除组织信息
	@RequestMapping("/delete")
	@ResponseBody
	public void deleteByPrimaryKey(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		String menuIdStr = request.getParameter("menuId");
		long menuId = Long.parseLong(menuIdStr);
		try {
			menuService.deleteByPrimaryKey(menuId);
			retMap.put("isSucess", true);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("isSucess", false);
		}

		Gson gson = new Gson();
		String resultJson = gson.toJson(retMap);
		this.flushResponse(response, resultJson);
	}
	
	/**
	 * 得到用户对应的授权的菜单集合
	 * @return
	 */
	@RequestMapping("/getAuthoMenuList")
	public ModelAndView getAuthoMenuList(HttpSession session){
		ModelAndView result = null;
		
		//一个员工能看到的菜单=员工对应的角色能看到的菜单+员工所在的部门对应的角色能看到的菜单
		User user = (User) session.getAttribute("user");
		//取用户的id
		Long userId = user.getUserId();
		//取用户所在的部门的id
		Long orgId = user.getOrgId();
		
		//通过userId到角色用户关系表中，通过userId查询roleId
		String userRoleIds = userSerivce.getRolesByUserId(userId);
		//通过orgId到角色组织关系表中，通过orgId查询roleId
		String orgRoleIds = userSerivce.getRolesByOrgId(orgId);
		//1,5,7
		
		//拼接roleId
		String roleIds = new String();
		if(userRoleIds!=null&&!userRoleIds.equals("")&&orgRoleIds!=null&&!orgRoleIds.equals("")){
			roleIds = userRoleIds+","+orgRoleIds;
		}else if(userRoleIds!=null&&!userRoleIds.equals("")){
			roleIds= userRoleIds;
		}else if(orgRoleIds!=null&&!orgRoleIds.equals("")){
			roleIds = orgRoleIds;
		}else{
			
		}
		//拆分
		List<Menu> menuList = new ArrayList<>();
		if(roleIds.length()>0){
			Map<String, Object> paramMap = new HashMap<>();
			String[] roleIdsArray = roleIds.split(",");
			paramMap.put("roleIds", roleIdsArray);
			menuList = menuService.getMenuListByRoleIds(paramMap);
			result = new ModelAndView("common/left");
			result.addObject("menuList", menuList);
		}		
		return result;
	}

}
