package com.qianfeng.application.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.qianfeng.application.model.Menu;
import com.qianfeng.application.model.Org;
import com.qianfeng.application.model.User;
import com.qianfeng.application.service.IOrgService;
import com.qianfeng.application.service.IUserService;
import com.qianfeng.application.service.IMenuService;
import com.qianfeng.common.controller.BaseController;

@Controller
@RequestMapping("/tree")
public class TreeController extends BaseController {

	@Autowired
	private IOrgService orgService;

	@Autowired
	private IMenuService menuService;
	
	@Autowired
	private IUserService userService;

	//通过父组织id查询该组织所属的子组织集合
	@RequestMapping("/orgSubList")
	public void loadTreeDate(HttpServletRequest request, HttpServletResponse response) {
		String orgParentId = request.getParameter("id");
		if (orgParentId == null || "".equals(orgParentId)) {
			orgParentId = "-1";
		}

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("orgParentId", orgParentId);

		List<Map<String, Object>> list = new ArrayList<>();
		List<Org> orgList = orgService.getOrgListByOrgParentId(paramMap);
		for (int i = 0; i < orgList.size(); i++) {
			Map<String, Object> orgMap = new HashMap<>();
			Org org = orgList.get(i);
			orgMap.put("id", org.getOrgId());
			orgMap.put("name", org.getOrgName());
			orgMap.put("isParent", true);

			list.add(orgMap);
		}

		Gson gson = new Gson();
		String responseContent = gson.toJson(list);
		this.flushResponse(response, responseContent);
	}

	// 展示菜单类型为目录的
	@RequestMapping("/menuDirSubList")
	public void menuDirSubList(HttpServletRequest request, HttpServletResponse response) {
		String menuParentId = request.getParameter("id");
		if (menuParentId == null || "".equals(menuParentId)) {
			menuParentId = "-1";
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("menuParentId", menuParentId);

		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		List<Menu> menuList = menuService.queryMenuDirListByMenuParentId(paramMap);
		for (int i = 0; i < menuList.size(); i++) {
			Map<String, Object> retMap = new HashMap<String, Object>();
			Menu menu = menuList.get(i);
			retMap.put("id", menu.getMenuId());
			retMap.put("name", menu.getMenuName());
			// isParent树的一个属性，true表示为目录图标,false表示文件图标
			retMap.put("isParent", true);
			retList.add(retMap);
		}
		Gson gson = new Gson();
		String resultJson = gson.toJson(retList);
		this.flushResponse(response, resultJson);
	}
	
	/**
	 * 加载菜单树，目录，菜单
	 */
	@RequestMapping("/menuSubList")
	public void menuSubList(HttpServletRequest request,HttpServletResponse response){
		String menuParentId = request.getParameter("id");
		if (menuParentId == null || "".equals(menuParentId)) {
			menuParentId = "-1";
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("menuParentId", menuParentId);
		
		List<Map<String, Object>> list= new ArrayList<>();
		List<Menu> menuList = menuService.queryMenuListByMenuParentId(paramMap);
		for(int i=0;i<menuList.size();i++){
			Map<String, Object> menuMap = new HashMap<>();
			Menu menu = menuList.get(i);
			menuMap.put("id", menu.getMenuId());
			menuMap.put("name", menu.getMenuName());
			
			if(menu.getMenuType()==1){
				menuMap.put("isParent", true);
			}
			
			if(menu.getMenuType()==2){
				menuMap.put("isParent", false);
			}
			
			list.add(menuMap);
		}
		Gson gson = new Gson();
		String resultJson = gson.toJson(list);
		this.flushResponse(response, resultJson);
		
	}
	
	@RequestMapping("/userSubList")
	public void addAuthUser(HttpServletRequest request,HttpServletResponse response){
		String orgParentId = request.getParameter("id");
		if(orgParentId==null || "".equals(orgParentId)){
			orgParentId="-1";
		}
		
		
		Map<String, Object> map = new HashMap<>();
		map.put("orgParentId", orgParentId);
		
		List<Map<String, Object>> list = new ArrayList<>();
		List<Org> orgList = orgService.getOrgListByOrgParentId(map);
		for(int i=0;i<orgList.size();i++){
			Map<String, Object> orgMap = new HashMap<>();
			Org org = orgList.get(i);
			orgMap.put("id", org.getOrgId());
			orgMap.put("name", org.getOrgName());
			orgMap.put("isParent", true);
			
			list.add(orgMap);
		}
		
		int orgId =Integer.parseInt(orgParentId);
		List<User> userList = userService.getUserByOrgId(orgId);
		for(int i=0;i<userList.size();i++){
			Map<String, Object> userMap = new HashMap<>();
			User user = userList.get(i);
			userMap.put("id", user.getUserId());
			userMap.put("name", user.getUserChName());
			userMap.put("isParent", false);
			
			list.add(userMap);
		}
		
		Gson gson = new Gson();
		String resultJson = gson.toJson(list);
		this.flushResponse(response, resultJson);
	}
}
