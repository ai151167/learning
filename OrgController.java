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
import com.qianfeng.application.model.Orgs;
import com.qianfeng.application.service.IOrgService;
import com.qianfeng.common.controller.BaseController;

@Controller
@RequestMapping("/org")
public class OrgController extends BaseController {

	@Autowired
	private IOrgService orgService;
		
	/**
	 * 跳转到组织管理页面
	 * @param request
	 * @param response
	 */
	
	@RequestMapping("/orgMana")
	public String toMana(){
		return "org/orgMana";
	}

	@RequestMapping("/queryOrgListByOrgParentId")
	public void getOrgList(HttpServletRequest request, HttpServletResponse response) {
		// {"parentId":1}
		Map<String, Object> map = this.getParam(request);
		List<Org> list = orgService.getOrgListByOrgParentId(map);

		Gson gson = new Gson();
		String responseData = gson.toJson(list);

		this.flushResponse(response, responseData);
	}

	/**
	 * 组织分页信息
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/orgPage")
	public ModelAndView getPage(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<>();
		//把带过来的请求参数放在map对象中
		Map<String, Object> paramMap = this.getParam(request);
		//查询分页数据和条数，并且装在map中返回
		resultMap = orgService.getOrgPage(paramMap);

		List<Org> orgList = (List<Org>) resultMap.get("orgList");
		int count = Integer.parseInt(resultMap.get("count").toString());

		ModelAndView result = new ModelAndView("org/orgList");
		result.addObject("orgList", orgList);
		result.addObject("count", count);
		return result;
	}
	
	/**
	 * 查询页码
	 * @param request
	 * @return
	 */
	@RequestMapping("/getPageNumber")
	public ModelAndView getOrgNumber(HttpServletRequest request){
		ModelAndView result = new ModelAndView("org/orgPageNumber");
		int total = Integer.parseInt(request.getParameter("total"));
		int startIndex = Integer.parseInt(request.getParameter("startIndex"));
		int pageSize = Integer.parseInt(request.getParameter("pageSize"));
		return this.getPageNumberInfo(total, startIndex, pageSize, result);
	}
	
	/**
	 * 添加组织
	 */
	
	@RequestMapping("/addOrg")
	public void addOrg(Org org,HttpServletResponse response){
		Map<String, Object> result = new HashMap<>();
		try {
			orgService.add(org);
			result.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("isSuccess", false);
		}
		
		Gson gson = new Gson();
		String responseContent = gson.toJson(result);
		this.flushResponse(response, responseContent);
	}
	
	@RequestMapping("/queryOrg")
	public void queryOrg(int orgId,HttpServletResponse response){
		Map<String, Object> map = new HashMap<>();
		Orgs org =orgService.queryOrgById(orgId);
		map.put("org", org);
		Gson gson = new Gson();
		String responseContent = gson.toJson(map);
		this.flushResponse(response, responseContent);
	}
	
	@RequestMapping("/updateOrg")
	public void updateOrg(Org org,HttpServletResponse response){
		Map<String, Object> result = new HashMap<>();
		try {
			orgService.update(org);
			result.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("isSuccess", false);
		}
		
		Gson gson = new Gson();
		String responseContent = gson.toJson(result);
		this.flushResponse(response, responseContent);
	}
	
	@RequestMapping("/delOrg")
	public void del(long orgId,HttpServletResponse response){
		Map<String, Object> result = new HashMap<>();
		try {
			orgService.delOrg(orgId);
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
