package com.qianfeng.application.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.explorer.ui.form.StringFormPropertyRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.qianfeng.application.dao.LeaveBillMapper;
import com.qianfeng.application.model.LeaveBill;
import com.qianfeng.application.model.User;
import com.qianfeng.application.service.WorkFlowService;
import com.qianfeng.common.WorkflowBean;

@Controller
@RequestMapping("/workflow")
public class WorkFlowController {

	@Autowired
	private WorkFlowService workFlowService;
	
	@Autowired
	private LeaveBillMapper leaveBillMapper;

	@RequestMapping("/todeployHome")
	public ModelAndView todeployHome() {
		ModelAndView result = new ModelAndView("views/workflow/workflowPage");
		// 1.流程布署信息
		List<Deployment> depList = workFlowService.queryDepolyMentList();
		// 2.流程定义信息
		List<ProcessDefinition> pdList = workFlowService.queryProcessDefintionList();
		result.addObject("depList", depList);
		result.addObject("pdList", pdList);

		return result;
	}

	/**
	 * 布署流程
	 * 
	 * @param bean
	 * @return
	 */
	@RequestMapping("/newdeploy")
	public String newdeploy(WorkflowBean bean) {
		// 得到上传布署规则的文件
		MultipartFile file = bean.getFile();
		// 得到布署文件的名称
		String filename = bean.getFilename();
		workFlowService.addDeployMent(file, filename);
		return "redirect:/workflow/todeployHome";
	}

	/**
	 * 删除流程布署
	 * 
	 * @param bean
	 * @return
	 */
	@RequestMapping("/delDeployment")
	public String delDeployment(WorkflowBean bean) {
		String deploymentId = bean.getDeploymentId();
		workFlowService.delDeployment(deploymentId);
		return "redirect:/workflow/todeployHome";
	}

	/**
	 * 查看流程图
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/proccessImage")
	public void proccessImage(WorkflowBean bean, HttpServletResponse response) throws IOException {
		// 布署ID
		String deploymentId = bean.getDeploymentId();
		// 得到图片的名称
		String imageName = bean.getImageName();
		InputStream inputStream = workFlowService.getImageInputStream(deploymentId, imageName);
		OutputStream outputStream = response.getOutputStream();
		for (int b = -1; ((b = inputStream.read()) != -1);) {
			outputStream.write(b);
		}
		outputStream.close();
		inputStream.close();
	}

	/**
	 * 启动申请流程 1.更新请假单状态 2.动态设置任务办理人 3.让业务关联流程
	 */
	@RequestMapping("/startProcess")
	public String startProcess(WorkflowBean bean, HttpSession session) {
		workFlowService.startProcess(bean, session);
		return "redirect:/workflow/listTask";
	}

	/**
	 * 通过用户的名称，查询任务列表
	 * 
	 * @return
	 */
	@RequestMapping("/listTask")
	public ModelAndView listTask(HttpSession session) {
		ModelAndView result = new ModelAndView("views/workflow/task");
		User user = (User) session.getAttribute("user");
		String userChName = user.getUserChName();
		List<Task> taskList = workFlowService.queryTaskListByUserChName(userChName);
		result.addObject("taskList", taskList);
		return result;
	}

	/**
	 * 跳转到办理任务的url
	 * 
	 */
	@RequestMapping("/viewTaskForm")
	public String viewTaskForm(WorkflowBean bean) {
		String taskId = bean.getTaskId();
		String url = workFlowService.getUrlByTaskId(taskId);
		url += "?taskId=" + taskId;
		return "redirect:" + url;/// workflow/audit?taskId=taskId
	}

	/**
	 * 办理任务
	 * 
	 * @return
	 */
	@RequestMapping("/audit")
	public ModelAndView aduitTask(WorkflowBean bean) {
		ModelAndView result = new ModelAndView("views/workflow/taskForm");
		// 通过任务id去查询请假单对象信息
		String taskId = bean.getTaskId();
		LeaveBill lb = workFlowService.getLeaveBillByTaskId(taskId);
		result.addObject("leaveBill", lb);
		result.addObject("taskId", taskId);
		// 查询连线
		List<String> outComeList = workFlowService.getOutComeListByTaskId(taskId);
		result.addObject("outComeList", outComeList);
		// 批注
		List<Comment> commentList = workFlowService.queryCommentListByTaskId(taskId);
		result.addObject("commentList", commentList);
		return result;
	}
	
	/**
	 * 完成任务
	 */
	@RequestMapping("/submitTask")
	public String completeTask(WorkflowBean bean,HttpSession session){
		workFlowService.completeTask(bean,session);
		return "redirect:/workflow/listTask";
	}
	
	
	/**
	 * 查看审核记录
	 */
	@RequestMapping("/showHisComment")
	public ModelAndView showHisComment(WorkflowBean bean){
		//通过请假单id得到请假单对象
		Long id = bean.getId();
		LeaveBill leaveBill = leaveBillMapper.queryLeaveBillById(id);
		ModelAndView result = new ModelAndView("views/workflow/taskFormHis");
		result.addObject("lb", leaveBill);
		
		//查询批注信息
		List<Comment> commentList = workFlowService.queryHisCommentList(id);
		result.addObject("commentList", commentList);
		return result;
	}
	
	/**
	 * 查看当前流程图
	 */
	@RequestMapping("/viewCurrentProcessImage")
	public ModelAndView viewCurrentProcessImage(WorkflowBean bean){
		
		ModelAndView result = new ModelAndView("views/workflow/image");
		ProcessDefinition pd = workFlowService.getProcessDefnition(bean.getTaskId());
		result.addObject("deploymentId", pd.getDeploymentId());
		result.addObject("imageName", pd.getDiagramResourceName());
		
		//得到坐标注map
		Map<String, Object> zuobiao = workFlowService.getZuoBiao(bean.getTaskId());	
		result.addObject("acs", zuobiao);
		return result;
	}
}
