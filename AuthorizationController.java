package com.qianfeng.application.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.qianfeng.application.model.Org;
import com.qianfeng.application.model.Role;
import com.qianfeng.application.model.RoleMenuRel;
import com.qianfeng.application.model.RoleOrgRel;
import com.qianfeng.application.model.RoleUserRel;
import com.qianfeng.application.service.IOrgService;
import com.qianfeng.application.service.IUserService;
import com.qianfeng.application.service.IMenuService;
import com.qianfeng.application.service.IRoleService;
import com.qianfeng.common.controller.BaseController;

@Controller
@RequestMapping("/authorization")
public class AuthorizationController extends BaseController {

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IOrgService orgSerivce;
	
	@Autowired
	private IMenuService menuService;
	
	@Autowired
	private IUserService userService;

	@RequestMapping("/authorizationMana")
	public String toMana() {
		return "authorization/authorizationMana";
	}

	/**
	 * 获取角色列表
	 * @param response
	 */
	@RequestMapping("/getRoleList")
	public void getRoleList(HttpServletResponse response) {
		List<Role> roleList = roleService.getRoleList();
		Gson gson = new Gson();
		String responseContent = gson.toJson(roleList);
		this.flushResponse(response, responseContent);
	}

	/**
	 * 加载授权组织的分页信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/queryAuthOrg")
	public ModelAndView queryAuthOrg(HttpServletRequest request) {
		Map<String, Object> paramMap = this.getParam(request);
		Map<String, Object> resultMap = orgSerivce.getAuthOrgPage(paramMap);

		List<Org> orgList = (List<Org>) resultMap.get("orgList");
		int count = (int) resultMap.get("count");

		ModelAndView result = new ModelAndView("authorization/orgList");
		result.addObject("orgList", orgList);
		result.addObject("count", count);
		return result;
	}
	
	/**
	 * 加载授权组织的页码
	 * @param request
	 * @return
	 */
	@RequestMapping("/queryAuthOrgNumber")
	public ModelAndView queryAuthOrgNumber(HttpServletRequest request){
		int total =Integer.parseInt(request.getParameter("total"));
		int startIndex =Integer.parseInt(request.getParameter("startIndex"));
		int pageSize =Integer.parseInt(request.getParameter("pageSize"));
		ModelAndView result = new ModelAndView("authorization/orgPageNumber");
		return this.getPageNumberInfo(total, startIndex, pageSize, result);
	}
	
	/**
	 * 删除授权组织
	 */
	@RequestMapping("/delRoleOrgRel")
	public void delAuthOrg(RoleOrgRel rel,HttpServletResponse response){
		Map<String, Object> result = new HashMap<>();
		try {
			orgSerivce.delAuthOrg(rel);
			result.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("isSuccess", false);
		}
		Gson gson = new Gson();
		String responseContent = gson.toJson(result);
		this.flushResponse(response, responseContent);
		
	}
	
	/**
	 * 授权菜单
	 */
	@RequestMapping("/addRoleMenuRel")
	public void AddauthoMenu(RoleMenuRel rel,HttpServletResponse response){
		Map<String, Object> result = new HashMap<>();
		try {
			menuService.addAuthMenu(rel);
			result.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("isSuccess", false);
		}
		Gson gson = new Gson();
		String responseContent = gson.toJson(result);
		this.flushResponse(response, responseContent);
	}
	
	/**
	 * 授权组织
	 */
	@RequestMapping("/addRoleOrgRel")
	public void AddAuthOrg(RoleOrgRel rel,HttpServletResponse response){
		Map<String, Object> result = new HashMap<>();
		try {
			orgSerivce.addAuthOrg(rel);
			result.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("isSuccess", false);
		}
		Gson gson = new Gson();
		String responseContent = gson.toJson(result);
		this.flushResponse(response, responseContent);
	}
	
	/**
	 * 授权人员
	 */
	@RequestMapping("/addRoleUserRel")
	public void AddAuthUser(RoleUserRel rel,HttpServletResponse response){
		Map<String, Object> result = new HashMap<>();
		try {
			userService.addAuthUser(rel);
			result.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("isSuccess", false);
		}
		Gson gson = new Gson();
		String responseContent = gson.toJson(result);
		this.flushResponse(response, responseContent);
	}
	
	
	
}
