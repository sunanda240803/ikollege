package com.iitm.hosteldine.controller.collegeInfo;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.collegeInfo.FaqDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.collegeInfo.FaqService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "${url.faq}")
@RequiredArgsConstructor
public class FaqController {
    private final CommonResponseUtil commonResponseUtil;
    private final FaqService faqService;


    @Value("${url.faq}")
    private String baseUrl;

    @GetMapping
    public String getFaqList(PaginationForm form,ModelMap map, HttpServletRequest request) {
        Page<FaqDto> faqList = faqService.getFaqList(form);
        commonResponseUtil.updateCommonModelAttributes(map, request,faqList,form);
        return HTMLPage.FAQ_LIST;
    }

    @GetMapping(value = "${id}")
    public String getFaqById(@PathVariable("id") Long id, ModelMap model,HttpServletRequest request) {
        String formKey = "faqDto";
        FaqDto faqDto = commonResponseUtil.handleModalFormError(request,model,formKey, FaqDto.class);

        if (Objects.nonNull(id) && id > 0) {
        	faqDto = faqService.getFaqById(id);
        }

        model.addAttribute(formKey, faqDto);
        return HTMLPage.FAQ_MODAL;
    }

    @PostMapping()
    public String saveOrUpdateFaq(@Valid @ModelAttribute FaqDto faqDto, BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()){
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes,bindingResult,faqDto);
            return "redirect:" + baseUrl;
        }

        String status = faqService.saveOrUpdateFaq(faqDto);
        String message=status.equalsIgnoreCase(Constants.SAVED)?"message.faq.save":"message.faq.update";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes,message);
        return "redirect:" + baseUrl;
    }

    @DeleteMapping("${id}")
    public @ResponseBody BaseResponse deleteFaqById(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(faqService.deleteFaq(id));
        } catch (RecordNotExistsException e) {
            // Create a custom error response in case of an exception
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }
}
