package com.iitm.hosteldine.controller;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.ResetPasswordDto;
import com.iitm.hosteldine.service.ResetPasswordService;
import com.iitm.hosteldine.util.HTMLPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.reset.password}")
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

	@GetMapping
	public String getResetPassword(ModelMap map, HttpServletRequest request) {
		map.addAttribute("resetPasswordDto", new ResetPasswordDto());
		return HTMLPage.RESET_PASSWORD;
	}

    @PostMapping
    public @ResponseBody String resetPassword(@ModelAttribute ResetPasswordDto resetPasswordDto) throws Exception {
        String returnStatus = resetPasswordService.resetPassword(resetPasswordDto);
        return switch (returnStatus) {
            case ModelConstants.SUCCESS -> ModelConstants.SUCCESS;
            case ModelConstants.FAILURE -> ModelConstants.FAILURE;
            case ModelConstants.PASSWORD_MISMATCH -> ModelConstants.PASSWORD_MISMATCH;
            default -> throw new Exception("Unexpected value: " + returnStatus);
        };
    }
}
