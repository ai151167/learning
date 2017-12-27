package com.qianfeng.application.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.qianfeng.application.model.User;
import com.qianfeng.application.service.IUserService;
import com.qianfeng.common.controller.BaseController;

@Controller
public class LoginController extends BaseController {

	@Autowired
	private IUserService userService;

	/***
	 * 跳转到登录页面
	 * 
	 * @return
	 */
	@RequestMapping("/toLogin")
	public String toLogin() {
		return "common/login";
	}

	/**
	 * 检验用户名和密码
	 * 
	 * @return
	 */
	@RequestMapping("/loginCheck")
	public ModelAndView loginCheck(HttpServletRequest request) {
		ModelAndView result = null;
		String userName = request.getParameter("userName");
		String userPassword = request.getParameter("userPassword");

		User user = userService.checkUserIsExits(userName, userPassword);
		if (user != null) {
			// 保存用户对象
			request.getSession().setAttribute("user", user);
			result = new ModelAndView("common/main");
			result.addObject("user", user);
		} else {
			result = new ModelAndView("common/login");
		}
		return result;

	}

	@RequestMapping("/frame")
	public String toFrame(HttpSession session) {
		String result = null;
		User user = (User) session.getAttribute("user");
		if (user != null) {
			result = "common/frame";
		} else {
			result = "common/login";
		}
		return result;
	}
}
