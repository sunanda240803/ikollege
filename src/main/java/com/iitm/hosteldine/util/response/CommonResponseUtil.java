package com.iitm.hosteldine.util.response;

import com.iitm.hosteldine.config.DynamicSecurityService;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.controller.CommonController;
import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.dto.ResetPasswordDto;
import com.iitm.hosteldine.exception.RecordAlreadyExistsException;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.FooterForm;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.lang.reflect.Method;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommonResponseUtil {

    private static MessageSource messageSource;

    @Value("${backend.validation.button}")
    private String backendValidationButton;
    private DynamicSecurityService dynamicSecurityService;
    private MenuService menuService;
    private final CommonController commonController;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        CommonResponseUtil.messageSource = messageSource;
    }

    public void updateCommonModelAttributes(ModelMap model, HttpServletRequest request, 
    		Page<?> paginationList, PaginationForm form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authenticated: {}", authentication.isAuthenticated());

        HeaderForm header = populateHeaderForm(request, authentication);
        model.addAttribute("headerForm", header);
        model.addAttribute("backendValidation", backendValidationButton);

        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        BaseResponse saveResponse = flashInputMap != null && flashInputMap.get(Constants.RESPONSE) != null
                ? ((BaseResponse) flashInputMap.get(Constants.RESPONSE))
                : null;

        FooterForm footerForm = FooterForm.defaultForm(saveResponse);
        if (footerForm != null) {
            model.addAttribute(Constants.FOOTER_FORM, footerForm);
        }

        Boolean modalError = flashInputMap != null && flashInputMap.get(Constants.MODAL_ERROR) != null
                ? ((Boolean) flashInputMap.get(Constants.MODAL_ERROR))
                : null;
        if (modalError != null) {
            model.addAttribute(Constants.MODAL_ERROR, true);
            request.getSession().setAttribute(Constants.FORM, flashInputMap.get(Constants.FORM));
            request.getSession().setAttribute(Constants.FORM_ERROR, flashInputMap.get(Constants.FORM_ERROR));
        }

        commonController.updateCommonAttributes(model);
        model.addAttribute("resetPasswordDto", new ResetPasswordDto());

        if (form != null) {
            model.addAttribute("form", form);
            form.setCurrentUrl(header.getCurrentURL());
            form.setPaginationList(paginationList != null ? paginationList : new PageImpl<>(new ArrayList<>()));
            model.addAllAttributes(form.getAdditionalParam());
        }
    }

    public <T> void updateCommonModelAttributes2(ModelMap model, HttpServletRequest request,
    		List<T> paginationList, PaginationForm form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Authenticated: {}", authentication.isAuthenticated());

        HeaderForm header = populateHeaderForm(request, authentication);
        model.addAttribute("headerForm", header);
        model.addAttribute("backendValidation", backendValidationButton);

        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        BaseResponse saveResponse = flashInputMap != null && flashInputMap.get(Constants.RESPONSE) != null
                ? ((BaseResponse) flashInputMap.get(Constants.RESPONSE))
                : null;

        FooterForm footerForm = FooterForm.defaultForm(saveResponse);
        if (footerForm != null) {
            model.addAttribute(Constants.FOOTER_FORM, footerForm);
        }

        Boolean modalError = flashInputMap != null && flashInputMap.get(Constants.MODAL_ERROR) != null
                ? ((Boolean) flashInputMap.get(Constants.MODAL_ERROR))
                : null;
        if (modalError != null) {
            model.addAttribute(Constants.MODAL_ERROR, true);
            request.getSession().setAttribute(Constants.FORM, flashInputMap.get(Constants.FORM));
            request.getSession().setAttribute(Constants.FORM_ERROR, flashInputMap.get(Constants.FORM_ERROR));
        }

        commonController.updateCommonAttributes(model);
        model.addAttribute("resetPasswordDto", new ResetPasswordDto());

        if (form != null) {
            model.addAttribute("form", form);
            form.setCurrentUrl(header.getCurrentURL());
            form.setList(paginationList != null ? paginationList : new ArrayList<T>());
            model.addAllAttributes(form.getAdditionalParam());
        }
    }
    
    private HeaderForm populateHeaderForm(HttpServletRequest request, Authentication authentication) {
        HeaderForm header = HeaderForm.defaultForm(request, dynamicSecurityService);
        List<MenuListDto> rawMenuList = dynamicSecurityService.getMenusForRole(authentication, null);
        List<MenuListDto> sortedList = menuService.hierarchySort(rawMenuList, false);

        if (!sortedList.isEmpty()) {
            long dashboardId = SecurityCtxUtil.dashboardId();
            if (sortedList.size() == 1) {
                dashboardId = 0L;
            }
            if (dashboardId == 0L && authentication.getPrincipal() instanceof MyUserDetails user) {
                user.setDashboardId(sortedList.getFirst().getMenuId());
            }

            List<MenuListDto> dashboardList = sortedList.stream()
                    .filter(it -> it.getMenuId().equals(SecurityCtxUtil.dashboardId()))
                    .toList();

            if (!dashboardList.isEmpty()) {
                header.setDashboardList(sortedList);
                MenuListDto dashboardMenu = dashboardList.getFirst();
                if (dashboardMenu != null) {
                    if (dashboardMenu.getSubMenu() != null) {
                        header.setMenuList(dashboardMenu.getSubMenu());
                    }
                    if (dashboardMenu.getSubTab() != null) {
                        header.setTabList(dashboardMenu.getSubTab());
                    }
                    if (dashboardMenu.getSubReport() != null) {
                        header.setReportList(dashboardMenu.getSubReport());
                    }
                }
            }
        }
        return header;
    }
    
    public void updateSaveResponseByStatus(String saveStatus,RedirectAttributes redirectAttrs) {
    	BaseResponse baseResponse = new BaseResponse();
        String message, status;
        if (saveStatus != null) {
            message = saveStatus.equals(Constants.SAVED) ? "response.save.success" : "response.update.success";
            status = "response.status.success";
        } else {
            message = "response.save.error";
            status = "response.status.failure";
            redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, true);
        }
        baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
        baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));
        redirectAttrs.addFlashAttribute(Constants.RESPONSE, baseResponse);
    }
    
    public static BaseResponse generateDeleteResponseByStatus(boolean deleteStatus) {
    	BaseResponse baseResponse = new BaseResponse();
    	String message, status;
        if (deleteStatus) {
            message = "response.delete.success";
            status = "response.status.success";
        } else {
            message = "response.delete.error";
            status = "response.status.failure";
        }
        baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
        baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));
        return baseResponse;
    }
    
    public void updateSaveResponseByStatus(String saveStatus, RedirectAttributes redirectAttrs, String content) {
        updateResponse(saveStatus, redirectAttrs, content, "response.save.error");
    }

    public void updateSaveAndErrorResponseByStatus(String saveStatus, RedirectAttributes redirectAttrs, String content) {
        updateResponse(saveStatus, redirectAttrs, content, content);
    }

    public void updateResponse(String saveStatus, RedirectAttributes redirectAttrs, String successMessage, String errorMessage) {
		BaseResponse baseResponse = new BaseResponse();
		returnBaseResponse(baseResponse, saveStatus, redirectAttrs, successMessage, errorMessage);
		boolean isSuccess = saveStatus != null;
		String message = isSuccess ? successMessage : errorMessage;
		baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
	}
    
    public void updateResponse(String saveStatus, RedirectAttributes redirectAttrs, String successMessage, String errorMessage, Object[] args) {
		BaseResponse baseResponse = new BaseResponse();
		returnBaseResponse(baseResponse, saveStatus, redirectAttrs, successMessage, errorMessage);
		boolean isSuccess = saveStatus != null;
		String message = isSuccess ? successMessage : errorMessage;
        baseResponse.setMessage(messageSource.getMessage(message, args, Locale.getDefault()));
    }

	private void returnBaseResponse(BaseResponse baseResponse, String saveStatus, RedirectAttributes redirectAttrs,
			String successMessage, String errorMessage) {
		boolean isSuccess = saveStatus != null;
		String status = isSuccess ? "response.status.success" : "response.status.failure";
		if (!isSuccess) {
			redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, true);
		}
		baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));
		redirectAttrs.addFlashAttribute(Constants.RESPONSE, baseResponse);
	}

    public void getAdditionalParams(Map<String, String> allParams, PaginationForm form) {
        form.setHasSearchParam(false);
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String paramName = entry.getKey();
            String paramValue = entry.getValue();
            if (paramName.startsWith(Constants.ADDITIONAL_PARAM)) {
                form.getAdditionalParam().put(paramName.replace(Constants.ADDITIONAL_PARAM, ModelConstants.EMPTY_STRING), paramValue);
            } else {
                form.getAdditionalParam().put(paramName, paramValue);
            }
            if (paramValue != null && !paramValue.isEmpty() && paramName.startsWith(Constants.ADDITIONAL_PARAM) &&
                    !paramName.endsWith(Constants.PAGE) && !paramName.endsWith(Constants.SIZE)){
                form.setHasSearchParam(true);
            }
        }
    }

	public void updateCommonModelAttributes(ModelMap map, HttpServletRequest request) {
		updateCommonModelAttributes(map,request,null,null);
		
	}

    public void updateModalFormErrorAttributes(RedirectAttributes redirectAttributes, BindingResult bindingResult,Object form) {
        redirectAttributes.addFlashAttribute(Constants.FORM, form);
        redirectAttributes.addFlashAttribute(Constants.FORM_ERROR, bindingResult);
        redirectAttributes.addFlashAttribute(Constants.MODAL_ERROR, true);
    }

    @SuppressWarnings("unchecked")
    public <DTO> DTO handleModalFormError(HttpServletRequest request, ModelMap model, String formKey, Class<DTO> dtoClass) {
        DTO formDTO = Optional.ofNullable(request.getSession().getAttribute(Constants.FORM))
                .map(form -> (DTO) form)
                        .orElse(instantiate(dtoClass));

        request.getSession().removeAttribute(Constants.FORM);

        // Handle BindingResult errors
        Optional.ofNullable(request.getSession()
                .<BindingResult>getAttribute(Constants.FORM_ERROR))
                .ifPresent(errors -> {
                    model.addAttribute(Constants.BINDING_RESULT_DATA + formKey, errors);
                    request.getSession().removeAttribute(Constants.FORM_ERROR);
                });
        model.addAttribute("backendValidation",backendValidationButton);

        return formDTO;
    }

    private <DTO> DTO instantiate(Class<DTO> dtoClass) {
        try {
            // Check if the class has a "builder()" method
            Method builderMethod = findBuilderMethod(dtoClass);

            if (builderMethod != null) {
                Object builder = builderMethod.invoke(null);
                Method buildMethod = builder.getClass().getMethod("build");
                return dtoClass.cast(buildMethod.invoke(builder));
            } else {
                // Otherwise, use default constructor
                return dtoClass.getDeclaredConstructor().newInstance();
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error creating new instance of DTO: " + dtoClass.getName(), e);
        }
    }

    private <DTO> Method findBuilderMethod(Class<DTO> dtoClass) {
        try {
            return dtoClass.getMethod("builder"); // Look for builder() method
        } catch (NoSuchMethodException e) {
            return null; // No builder found
        }
    }
    
	public ResponseEntity<Object> handleValidationErrors(BindingResult result) {
		String message = messageSource.getMessage("response.save.error", null, Locale.getDefault());
		String status = messageSource.getMessage("response.status.failure", null, Locale.getDefault());
		Map<String, Object> errorResponse = new HashMap<>();

		StringBuffer errorMsg = new StringBuffer();
		errorMsg.append(message + ":<br><ul>");

		for (ObjectError error : result.getAllErrors()) {
			String errorMessage = resolveErrorMessage(error, error.getObjectName());
			if (error instanceof FieldError) {
				errorMsg.append("<li>").append(errorMessage).append("</li>");
			}
		}
		errorMsg.append("</ul>");
		errorResponse.put("code", HttpStatus.BAD_REQUEST.value());
		errorResponse.put("status", status);
		errorResponse.put("errors", errorMsg.toString());

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	private String resolveErrorMessage(ObjectError error, String fieldName) {
		String errorMsg = messageSource.getMessage(error.getCode(), null, error.getDefaultMessage(),
				Locale.getDefault());
		return errorMsg;
	}
	
	public void exceptionMessageHandling(Exception ex, RedirectAttributes redirectAttrs) {
        log.error(ex.getMessage(), ex);
        BaseResponse response;
        switch (ex) {
            case IllegalArgumentException iae -> response = new BaseResponse(iae.getMessage(), Constants.ERROR);
            case DataIntegrityViolationException dive -> response = new BaseResponse(getDataIntegrityViolationExceptionMsg(dive), Constants.ERROR);
            case ConstraintViolationException cve -> response = new BaseResponse(getConstraintViolationExceptionMsg(cve), Constants.ERROR);
            case IncorrectResultSizeDataAccessException irdae -> response = new BaseResponse(getIncorrectResultSizeDataAccessExceptionMsg(irdae), Constants.ERROR);
            case RuntimeException rex -> response = new BaseResponse(rex.getMessage(), Constants.ERROR);
            default -> response = new BaseResponse(getMessage("message.unexpected.error"), Constants.ERROR);
        }
        redirectAttrs.addFlashAttribute("response", response);
    }

    private static String getDataIntegrityViolationExceptionMsg(DataIntegrityViolationException dive) {
        String errorMessage = dive.getMostSpecificCause().getMessage();
        return switch (errorMessage) {
            case String msg when msg.contains("value too long for type character varying") ->
                    "One of the entered fields exceeds the allowed character limit. Please shorten the input and try again.";
            case String msg when msg.contains("duplicate key value") -> "Duplicate data already exists.";
            case String msg when msg.contains("null value in column") -> "Required field value is missing.";
            case String msg when msg.contains("violates foreign key constraint") -> "Invalid reference data provided.";
            default -> "An error occurred while processing the data. Please verify the entered details.";
        };
    }

    private static String getConstraintViolationExceptionMsg(ConstraintViolationException constraintViolationException) {
        return constraintViolationException.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("An error occurred while processing the data. Please verify the entered details.");
    }

    private static String getIncorrectResultSizeDataAccessExceptionMsg(IncorrectResultSizeDataAccessException incorrectResultSizeDataAccessException) {
        return incorrectResultSizeDataAccessException.getMostSpecificCause().getMessage();
    }

    public String getMessage(String message){
        return messageSource.getMessage(message, null, Locale.getDefault());
    }
    
	public void dynamicResponseByStatus(String saveStatus, RedirectAttributes redirectAttrs, String content) {
		BaseResponse baseResponse = new BaseResponse();
		String message, status;
		if (saveStatus.equals(ModelConstants.SUCCESS)) {
			message = content;
			status = "response.status.success";
		} else {
			message = content;
			status = "response.status.failure";
			redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, true);
		}
		baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
		baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));
		redirectAttrs.addFlashAttribute(Constants.RESPONSE, baseResponse);
	}
	
    public void updateHeaderForm(HttpServletRequest request, ModelMap model, String menuHeading, Boolean needAddNew) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HeaderForm header = populateHeaderForm(request, authentication);
        Optional.ofNullable(needAddNew).ifPresent(header::setNeedAddNew);
        Optional.ofNullable(menuHeading).ifPresent(header::setMenuHeading);
        model.addAttribute("headerForm", header);
    }
    
    public BaseResponse errorExceptionHandling(Exception ex){
        BaseResponse errorResponse = new BaseResponse();
        errorResponse.setMessage(ex.getMessage());
        if(ex instanceof RecordNotExistsException || ex instanceof RecordAlreadyExistsException){
            errorResponse.setStatus(Constants.FAILURE);
        }
        return errorResponse;
    }

    public <DTO> DTO getRedirectedValues(HttpServletRequest request, String attributeKey, Class<DTO> castClass) {
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        if (flashInputMap != null && flashInputMap.get(attributeKey) != null) {
            return castClass.cast(flashInputMap.get(attributeKey));
        }
        return null;
    }

    public void invalidAccess(RedirectAttributes redirectAttrs) {
        BaseResponse response = new BaseResponse();
        response.setStatus(Constants.ERROR);
        response.setMessage(getMessage("message.exception.invalid.access"));
        redirectAttrs.addFlashAttribute("response", response);
    }


    @Autowired
    public void setDynamicSecurityService(DynamicSecurityService dynamicSecurityService) {
        this.dynamicSecurityService = dynamicSecurityService;
    }

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }
    
    public static BaseResponse updateResponseByStatus(boolean updateStatus, String successContent, String failureContent) {
    	BaseResponse baseResponse = new BaseResponse();
    	String message, status;
        if (updateStatus) {
        	message = successContent;
            status = "response.status.success";
        } else {
        	message = failureContent;
            status = "response.status.failure";
        }
        baseResponse.setMessage(messageSource.getMessage(message, null, Locale.getDefault()));
        baseResponse.setStatus(messageSource.getMessage(status, null, Locale.getDefault()));
        return baseResponse;
    }
    
    public static BaseResponse updateResponseByValue(Object value) {
    	BaseResponse baseResponse = new BaseResponse();
    	baseResponse.setData(value);
        return baseResponse;
    }
}
