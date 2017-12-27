package com.qianfeng.application.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.qianfeng.application.model.LeaveBill;
import com.qianfeng.application.model.User;
import com.qianfeng.application.service.LeaveBillService;
import com.qianfeng.common.WorkflowBean;

@Controller
@RequestMapping("/leaveBill")
public class LeaveBillContrller {

	@Autowired
	private LeaveBillService leaveBillService;
	
	/***
	 * 跳转到请假单列表的页面
	 * @return
	 */
	@RequestMapping("/leaveBill_main")
	public ModelAndView toLeaveBill_main(HttpSession session){
		ModelAndView result = new ModelAndView("views/leaveBill/list");
		User user = (User) session.getAttribute("user");
		String userChName = user.getUserChName();
		List<LeaveBill> lbList = leaveBillService.getLeaveBillListByUserChName(userChName);
		result.addObject("lbList", lbList);
		return result;
	}
	
	/**
	 * 跳转到添加请假单的页面
	 * @return
	 */
	@RequestMapping("/toAddLeaveBill")
	public String toAddLeaveBill(){
		return "views/leaveBill/input";
	}
	
	/**
	 * 添加，修改请假单
	 * @return
	 */
	@RequestMapping("/saveLeaveBill")
	public String saveLeaveBill(LeaveBill lb,HttpSession session){
		leaveBillService.saveLeaveBill(lb,session);
		return "redirect:/leaveBill/leaveBill_main";
	}
	
	/**
	 * 跳转到添加请假单的界面，做回显操作
	 */
	@RequestMapping("/toUpdate")
	public ModelAndView toUpdate(WorkflowBean bean){
		ModelAndView result = new ModelAndView("views/leaveBill/input");
		Long id = bean.getId();
		LeaveBill lb = leaveBillService.queryLeaveBillById(id);
		result.addObject("lb", lb);
		return result;
	}
	
	@RequestMapping("/delete")
	public String delete(long id){
		leaveBillService.deleteById(id);
		return "redirect:/leaveBill/leaveBill_main";
	}
	
	
}
