package com.qianfeng.application.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.qianfeng.application.model.User;
import com.qianfeng.application.model.Users;
import com.qianfeng.application.service.IUserService;
import com.qianfeng.common.controller.BaseController;
import com.qianfeng.common.email.SendEmail;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	@Autowired
	private IUserService userService;
	
	@RequestMapping("/userMana")
	public String toUserMana(){
		return "user/userMana";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/userPage")
	public ModelAndView userPage(HttpServletRequest request){
		Map<String, Object> paramMap = this.getParam(request);
		Map<String, Object> resultMap = userService.getUserPage(paramMap);
		
		List<User> userList = (List<User>) resultMap.get("userList");
		int count =Integer.parseInt(resultMap.get("count").toString());
		
		ModelAndView result = new ModelAndView("user/userList");
		result.addObject("userList", userList);
		result.addObject("count", count);
		
		return result;		
	}
	
	@RequestMapping("/userPageNumber")
	public ModelAndView getUserPageNumber(HttpServletRequest request){
		int total =Integer.parseInt(request.getParameter("total"));
		int startIndex =Integer.parseInt(request.getParameter("startIndex"));
		int pageSize =Integer.parseInt(request.getParameter("pageSize"));
		ModelAndView result = new ModelAndView("user/userPageNumber");
		return this.getPageNumberInfo(total, startIndex, pageSize, result);
		
	}
	
	@RequestMapping("/addUser")
	public void addUser(User user,HttpServletResponse response,HttpServletRequest request){
		Map<String, Object> result = new HashMap<>();
		try {
			String birthday_temp = request.getParameter("userBirthday_tmp");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date birthday = simpleDateFormat.parse(birthday_temp);
			user.setUserBirthday(birthday);
			userService.add(user);
			result.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("isSuccess", false);
		}
		
		Gson gson = new Gson();
		String responseContent = gson.toJson(result);
		this.flushResponse(response, responseContent);
	}
	
	@RequestMapping("/queryUser")
	public void getUserById(int userId,HttpServletResponse response){
		Map<String, Object> map = new HashMap<>();
		Users user = userService.getUser(userId);
		map.put("user", user);
		Gson gson = new Gson();
		String responseContent = gson.toJson(map);
		this.flushResponse(response, responseContent);
	}
	
	@RequestMapping("/updateUser")
	public void update(User user,HttpServletResponse response,HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			String userBirthday_tmp = request.getParameter("userBirthday_tmp");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date birthday = sdf.parse(userBirthday_tmp);
			user.setUserBirthday(birthday);
			userService.updateUser(user);
			map.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("isSuccess", false);
		}
		Gson gson = new Gson();
		String responseContent = gson.toJson(map);
		this.flushResponse(response, responseContent);
	}
	
	@RequestMapping("/delete")
	public void deleleUser(long userId,HttpServletResponse response){
		Map<String, Object> map = new HashMap<>();
		try {
			userService.deleteUserById(userId);
			map.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("isSuccess", false);
		}
		Gson gson = new Gson();
		String responseContent = gson.toJson(map);
		this.flushResponse(response, responseContent);
	}
	
	/**
	 * 统计性别填充图表
	 */
	@RequestMapping("/getUserSexStatistics")
	public void getUserSexStatistics(HttpServletResponse response){
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> resultMap = new HashMap<>();
		try {
			list =  userService.getUserSexList();
			resultMap.put("list", list);
			resultMap.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("isSuccess", true);
		}
		
		Gson gson = new Gson();
		String responseContent = gson.toJson(resultMap);
		this.flushResponse(response, responseContent);
	}
	
	/**
	 * 统计员工来源
	 */
	@RequestMapping("/getUserStatistics")
	public void getUserStatistics(HttpServletResponse response){
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> resultMap = new HashMap<>();
		try {
			list = userService.getUserList();
			resultMap.put("list", list);
			resultMap.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("isSuccess", false);
		}
		Gson gson = new Gson();
		String responseContent = gson.toJson(resultMap);
		this.flushResponse(response, responseContent);
	}
	
	@RequestMapping("/sendEmail")
	public void sendEmail(HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> resultMap = new HashMap<>();
		try {
			String sendAddress = request.getParameter("sendAddress");
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			
			SendEmail.sendUserEmail(sendAddress,title,content);
			resultMap.put("isSuccess", true);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("isSuccess", false);
		}
		
		Gson gson = new Gson();
		String responseContent = gson.toJson(resultMap);
		this.flushResponse(response, responseContent);
	}
}
