package com.qianfeng.application.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.qianfeng.application.model.Role;
import com.qianfeng.application.service.IRoleService;
import com.qianfeng.common.controller.BaseController;


@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {

	@Resource(name = "roleServiceImpl")
	private IRoleService roleService;
	
	@RequestMapping("/roleMana")
	public ModelAndView toroleMana(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("/role/roleMana");
		return result;
	}

	//分页显示组织信息
	@SuppressWarnings("unchecked")
	@RequestMapping("/rolePage")
	public ModelAndView rolePage(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> paramMap = this.getParam(request);
		Map<String, Object> retMap = roleService.queryRolePage(paramMap);

		List<Role> roleList = (List<Role>) retMap.get("roleList");
		long total = Long.parseLong(retMap.get("total").toString());

		ModelAndView result = new ModelAndView("role/roleList");
		result.addObject("roleList", roleList);
		result.addObject("total", total);

		return result;
}
	//展示页码信息
	@RequestMapping("/getPageNumber")
	public ModelAndView getPageNumber(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("role/rolePageNumber");
		int total = Integer.parseInt(request.getParameter("total"));
		int startIndex = Integer.parseInt(request.getParameter("startIndex"));
		int pageSize = Integer.parseInt(request.getParameter("pageSize"));
		result = this.getPageNumberInfo(total, startIndex, pageSize, result);
		return result;
	}
	
	//添加信息
	@RequestMapping("/add")
	@ResponseBody
	public void insert(HttpServletRequest request,
			HttpServletResponse response, Role role) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			roleService.insert(role);
			retMap.put("isSucess", true);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("isSucess", false);
		}

		Gson gson = new Gson();
		String resultJson = gson.toJson(retMap);
		this.flushResponse(response, resultJson);
	}
	
	//查看组织信息
	@RequestMapping("/queryRole")
	@ResponseBody
	public void selectByPrimaryKey(HttpServletRequest request,
			HttpServletResponse response, long roleId) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			Role role = roleService.selectByPrimaryKey(roleId);
			retMap.put("role", role);
			retMap.put("isSucess", true);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("isSucess", false);
		}
		
		Gson gson = new Gson();
		String resultJson = gson.toJson(retMap);
		this.flushResponse(response, resultJson);
	}
	
	//修改组织信息
	@RequestMapping("/update")
	@ResponseBody
	public void updateByPrimaryKeySelective(HttpServletRequest request,
			HttpServletResponse response, Role role) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			roleService.updateByPrimaryKeySelective(role);
			retMap.put("isSucess", true);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("isSucess", false);
		}

		Gson gson = new Gson();
		String resultJson = gson.toJson(retMap);
		this.flushResponse(response, resultJson);
	}
	
	//删除组织信息
	@RequestMapping(method = RequestMethod.POST, value = "/delete")
	@ResponseBody
	public void deleteByPrimaryKey(HttpServletRequest request,
			HttpServletResponse response,long roleId) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			roleService.deleteByPrimaryKey(roleId);
			retMap.put("isSucess", true);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("isSucess", false);
		}
       
		Gson gson = new Gson();
		String resultJson = gson.toJson(retMap);
		this.flushResponse(response, resultJson);
	}

}
