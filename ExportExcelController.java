package com.qianfeng.application.controller;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qianfeng.application.service.IUserService;
import com.qianfeng.common.controller.BaseController;
import com.qianfeng.common.excel.ExcelUtil;
import com.qianfeng.common.excel.SimpleExportParameter;

@Controller
public class ExportExcelController extends BaseController {
	
	@Autowired
	private IUserService userService;

	@RequestMapping("/exportUserExcel")
	public void exportUserExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 最终生成的excel文档的名称
		String fileName = "";
		Map<String, Object> paramMap = this.getParam(request);
		Object fileName_temp = paramMap.get("fileName");

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String defaultDate = sdf.format(date);

		if (fileName_temp != null && !("").equals(fileName_temp.toString())) {
			// 员工信息.xls
			String fileChName = fileName_temp.toString();
			if (fileChName.endsWith(".xls") || fileChName.endsWith(".xlsx")) {
				fileChName = fileChName.substring(0, fileChName.lastIndexOf("."));
			}
			fileName = fileChName + defaultDate;
		} else {
			fileName = defaultDate;
		}
		// 统一加后缀 .xls
		fileName = fileName + ".xls";

		OutputStream outputStream = null;
		try {
			// 得到输出流
			outputStream = response.getOutputStream();

			// 设置响应头,对附件方式下载
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));

			// 创建一个excel文档对象
			Workbook workbook = new HSSFWorkbook();

			// 填充的excel文档
			this.fillUserExcel(workbook);

			// 通过输出流把文档对象输出
			workbook.write(outputStream);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}

	}

	/**
	 * 填充excel文档的内容
	 * @param workbook
	 */
	private void fillUserExcel(Workbook workbook) {
		SimpleExportParameter exportParameter = this.getSimpleExportParameter();
		ExcelUtil util = new ExcelUtil();
		Sheet sheet = workbook.createSheet();
		util.simpleExport(workbook, sheet, exportParameter);
	}

	/**
	 * 给类中的属性赋值 
	 * @return
	 */
	private SimpleExportParameter getSimpleExportParameter() {
		//数据库查询的字段名称要跟这里定义的名称一致
		String filedIds = "userChName,userSex,mobilePhone,provinceName,cityName,countyName,userBirthday";
		String filedNames = "姓名,性别,电话,省份,地市,区县,生日";
		String widths = "20,20,20,20,20,20,20";
		
		String title = "千锋员工信息";
		String sheetName = "员工信息";
		
		String[] ids = filedIds.split(",");
		String[] filedChNames = filedNames.split(",");
		String[] widthes = widths.split(",");
		
		List<Map<String, Object>> dataList = userService.queryColumnList();
		
		SimpleExportParameter sep = new SimpleExportParameter();
		sep.setTitle(title);
		sep.setTitleEn(sheetName);
		sep.setFieldsId(ids);
		sep.setFieldsName(filedChNames);
		sep.setWidths(widthes);
		sep.setDataList(dataList);
		return sep;
	}
}
