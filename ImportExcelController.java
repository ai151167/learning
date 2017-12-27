package com.qianfeng.application.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.qianfeng.application.service.IUserService;
import com.qianfeng.common.controller.BaseController;
import com.qianfeng.common.excel.ExcelUtil;
import com.qianfeng.common.excel.SimpleReadParameter;

@Controller
public class ImportExcelController extends BaseController {

	@Autowired
	private IUserService userSerivce;

	@RequestMapping("/uploadExcel")
	public void uploadExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 把这个request对象转换成spring的request
			MultipartHttpServletRequest httpServletRequest = (MultipartHttpServletRequest) request;
			// 得到上传的文件列表
			Map<String, MultipartFile> fileMap = httpServletRequest.getFileMap();
			// 遍历
			for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
				// 获取excel文档
				MultipartFile file = entry.getValue();
				// 得到输入流
				InputStream inputStream = file.getInputStream();
				// 创建一个excel文档对象
				Workbook workbook = WorkbookFactory.create(inputStream);
				// 读取execl文档内容
				//[{orgId=1, mobilePhone=13378696977, email=1642483368@qq.com, userSex=1, userName=wuyanzu, userChName=吴彦祖}, {orgId=1, mobilePhone=13378696988, email=1642483368@qq.com, userSex=3, userName=fangbingbing, userChName=范冰冰}]
				List<Map<String, Object>> list = readExcel(workbook);
				// 把读取到的数据插入到数据库
				userSerivce.insertSheetData(list);
				resultMap.put("isSuccess", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("isSuccess", false);
		}
		Gson gson = new Gson();
		String responseContent = gson.toJson(resultMap);
		this.flushResponse(response, responseContent);
	}

	/**
	 * 读取execl文档内容
	 * 
	 * @param workbook
	 * @return
	 */
	private List<Map<String, Object>> readExcel(Workbook workbook) {
		//
		SimpleReadParameter readParameter = this.getSimpleReadParamter();
		// 得到字段名称的集合
		String[] fieldNames = readParameter.getFieldsId();
		int startIndex = readParameter.getStartIndex();

		ExcelUtil util = new ExcelUtil();
		List<Map<String, Object>> list = new ArrayList<>();
		if (workbook != null && workbook.getNumberOfSheets() > 0) {
			Sheet sheet = workbook.getSheetAt(0);
			List<Map<String, Object>> sheetData = util.readSimple(sheet, startIndex, fieldNames);
			if(sheetData!=null && sheetData.size()>0){
				list.addAll(sheetData);
			}
		}
		return list;
	}

	private SimpleReadParameter getSimpleReadParamter() {
		SimpleReadParameter simpleReadParameter = new SimpleReadParameter();

		//
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("userChName,").append("mobilePhone,").append("email,").append("userSex,").append("userName,")
				.append("orgId,");
		String[] fieldNames = sBuffer.toString().split(",");
		simpleReadParameter.setFieldsId(fieldNames);
		simpleReadParameter.setStartIndex(2);
		return simpleReadParameter;
	}
}
