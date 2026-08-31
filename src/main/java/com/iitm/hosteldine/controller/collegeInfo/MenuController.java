package com.iitm.hosteldine.controller.collegeInfo;

import com.iitm.hosteldine.config.DynamicSecurityService;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.exception.RecordListEmptyException;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.MenuService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.menu.master}")
public class MenuController {
    private final DynamicSecurityService dynamicSecurityService;
    @Value("${url.menu.master}")
    private String menuContextPath;

    private final MenuService menuService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessageSource messageSource;

    @GetMapping
    public String getAllMenus(PaginationForm form, ModelMap modelMap, HttpServletRequest request) {
        Page<MenuListDto> menuPage = menuService.getMenuPage(form);
        commonResponseUtil.updateCommonModelAttributes(modelMap, request, menuPage, form);
        return HTMLPage.MENU_MASTER;
    }

    @GetMapping("/{id}")
    public String getMenuById(@PathVariable long id, ModelMap modelMap) throws RecordNotExistsException {
        MenuListDto menu = menuService.getMenuList(id);

        if (Optional.ofNullable(menu).isEmpty()) {
            throw new RecordNotExistsException(messageSource.getMessage("message.error.menu.notFound",
                    new Object[]{id}, Locale.getDefault()));
        }

        modelMap.addAttribute("menu", menu);
        List<String> menuTypes = menuService.getAllMenuTypes();
        modelMap.addAttribute("menuTypes", menuTypes);

        return HTMLPage.ADD_EDIT_MENU_MASTER;
    }

    @PostMapping
    public String createUpdateMenu(@ModelAttribute MenuListDto menuDto, RedirectAttributes redirectAttributes)
            throws RecordListEmptyException {
        String status = Optional.ofNullable(menuDto)
                .map(dto -> {
                    try {
                        return menuService.saveOrUpdateMenu(dto);
                    } catch (RecordNotExistsException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new RecordListEmptyException(messageSource.getMessage(
                        "message.error.menu.null", null, Locale.getDefault())));
        dynamicSecurityService.refreshPermissions();
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes);

        return Constants.REDIRECT + menuContextPath;
    }

    @DeleteMapping("/{id}")
    public @ResponseBody String deleteMenu(@PathVariable long id) throws RecordNotExistsException {
        menuService.deleteMenu(id);
        dynamicSecurityService.refreshPermissions();
        return "Success";
    }
}
