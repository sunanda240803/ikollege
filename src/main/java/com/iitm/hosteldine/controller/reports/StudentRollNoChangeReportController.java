package com.iitm.hosteldine.controller.reports;

import java.util.List;

import com.iitm.hosteldine.util.Utility;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.form.reports.StudentRollNoChangeForm;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("${url.student.roll.no.change}")
@RequiredArgsConstructor
class StudentRollNoChangeReportController {

    private final CommonResponseUtil commonResponseUtil;
    private final StudentRollNoChangeReportService studentRollNoChangeReportService;

    @GetMapping
    String getStudentRollChange(StudentRollNoChangeForm form, ModelMap model, HttpServletRequest request) {
        commonResponseUtil.updateCommonModelAttributes(model, request, null, form);
        List<StudentRollChangeRecord> studentRollNoChange = studentRollNoChangeReportService.getStudentRollNoChange(form);
        model.addAttribute("studentRollNoChangeList", studentRollNoChange);
        return HTMLPage.STUDENT_ROLL_NO_CHANGE_REPORT;
    }

    @GetMapping(value = "${url.approve}" + "${studentId}" + "${id}" + "${RequestId}")
    @ResponseBody BaseResponse approve(@PathVariable("studentId") String studentId, @PathVariable("id") String newRollNo,
                                       @PathVariable("RequestId") Long requestId) {
        try{
            String s = studentRollNoChangeReportService.approveStudentRollNoChange(studentId, newRollNo, requestId);
            BaseResponse response = new BaseResponse();
            response.setStatus(Constants.SUCCESS);
            response.setMessage(commonResponseUtil.getMessage("message.request.approved.successfully"));
            return response;
        }
        catch(Exception e){
            e.printStackTrace();
            return commonResponseUtil.errorExceptionHandling(e);
        }
    }
    
	@GetMapping("${url.download}" + "${fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws Exception {
		ByteArrayResource resource = studentRollNoChangeReportService.loadFile(fileName);
        return Utility.prepareDownloadFile(resource);
	}
}
