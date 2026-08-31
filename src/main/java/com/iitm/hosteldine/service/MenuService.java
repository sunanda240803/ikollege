package com.iitm.hosteldine.service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.MenuListMapper;
import com.iitm.hosteldine.model.MenuListEntity;
import com.iitm.hosteldine.repository.MenuListRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuListRepository menuListRepository;
    private final MessageSource messageSource;

    public ArrayList<MenuListDto> getMenuList(String menuType) {
        String userId = SecurityCtxUtil.userId().toUpperCase();
        ArrayList<MenuListDto> returnMenuListDto = null;
        List<MenuListEntity> menuListEntities = menuListRepository.getDashboardList(userId, menuType, ModelConstants.STATUS_ACTIVE);
        if (!menuListEntities.isEmpty()) {
            returnMenuListDto = new ArrayList<>();
            for (MenuListEntity menuListEntity : menuListEntities) {
                MenuListDto dto = MenuListMapper.INSTANCE.fromMenuListEntity(menuListEntity);
                returnMenuListDto.add(dto);
            }
        }
        return returnMenuListDto;
    }

    public ArrayList<MenuListDto> getSubDashboardById(Long menuOrderId) {
        ArrayList<MenuListDto> resultList = null;
        List<MenuListEntity> tabList = menuListRepository.getSubDashboardById(menuOrderId);
        if (!tabList.isEmpty()) {
            resultList = new ArrayList<>();
            for (MenuListEntity menuListEntity : tabList) {
                MenuListDto menuListDto = MenuListMapper.INSTANCE.fromMenuListEntity(menuListEntity);
                resultList.add(menuListDto);
            }
        }
        return resultList;
    }

    public MenuListDto getMenuList(Long id) {
        MenuListDto menuListDto = null;

        if (id == 0) {
            return new MenuListDto();
        } else {
            Optional<MenuListEntity> optionalMenuListEntity = menuListRepository.findById(id);
            if (optionalMenuListEntity.isPresent()) {
                menuListDto = MenuListMapper.INSTANCE.fromMenuListEntity(optionalMenuListEntity.get());
            }
            return menuListDto;
        }
    }

    public MenuListDto getMenuListByMainMenuId(int mainMenuId) {
        MenuListDto menuListDto = null;
        Optional<MenuListEntity> optionalMenuListEntity = menuListRepository.findByMenuOrderAndMainMenuOrder(mainMenuId, 1);
        if (optionalMenuListEntity.isPresent()) {
            menuListDto = MenuListMapper.INSTANCE.fromMenuListEntity(optionalMenuListEntity.get());
        }
        return menuListDto;
    }

    public MenuListDto getMenuListByTypeAndURL(String menuType, String urlPath) {
        MenuListDto menuListDto = null;
        Optional<MenuListEntity> optionalMenuListEntity = menuListRepository.findByMenuTypeAndUrlPath(menuType, urlPath);
        if (optionalMenuListEntity.isPresent()) {
            menuListDto = MenuListMapper.INSTANCE.fromMenuListEntity(optionalMenuListEntity.get());
        }
        return menuListDto;
    }

    public Page<MenuListDto> getMenuPage(PaginationForm form) {
        Page<MenuListEntity> menuEntities;
        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("menuOrder")
                .and(Sort.by("mainMenuOrder"))
                .and(Sort.by("subMenuOrder"))
                .and(Sort.by("subSubMenuOrder")));

        if (StringUtils.isEmpty(form.getSearch())) {
            menuEntities = menuListRepository.findAll(pageable);
        } else {
            menuEntities = menuListRepository.getMenusBySearchKey(StringUtils.toRootLowerCase(form.getSearch()), pageable);
        }

        return menuEntities.map(MenuListMapper.INSTANCE::fromMenuListEntity);
    }

    public List<String> getAllMenuTypes() {
        return menuListRepository.getMenuTypes();
    }

    public String saveOrUpdateMenu(MenuListDto dto) throws RecordNotExistsException {
        Optional<MenuListEntity> optEntity;

        if (dto.getMenuId() == null || dto.getMenuId() == 0) {
            menuListRepository.save(MenuListMapper.INSTANCE.toMenuListEntity(dto));
            return Constants.SAVED;
        } else {
            optEntity = menuListRepository.findByMenuId(dto.getMenuId());

            if (optEntity.isEmpty()) {
                throw new RecordNotExistsException(messageSource.getMessage("message.error.menu.notFound",
                        new Object[]{dto.getMenuId()}, Locale.getDefault()));
            }

            menuListRepository.save(MenuListMapper.INSTANCE.toMenuListEntity(dto));
            return Constants.UPDATED;
        }
    }

    public void deleteMenu(long id) throws RecordNotExistsException {
        Optional<MenuListEntity> optEntity = menuListRepository.findByMenuIdAndActiveFlag(id, "Y");

        if (optEntity.isEmpty()) {
            throw new RecordNotExistsException(messageSource.getMessage("message.error.menu.notFound",
                    new Object[]{id}, Locale.getDefault()));
        }

        MenuListEntity entity = optEntity.get();
        entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
        menuListRepository.save(entity);
    }

    public List<MenuListDto> hierarchySort(List<MenuListDto> rawMenuList, boolean flatList) {
        List<MenuListDto> dashboardList = rawMenuList.stream().filter(it -> it.getMenuType().equals("dashboard")).toList();
        if (!dashboardList.isEmpty()) {
            dashboardList.forEach(dashboard -> getSubMenu(rawMenuList, dashboard, flatList));
        }
        return dashboardList;
    }

    private List<MenuListDto> getSubMenu(List<MenuListDto> rawMenuList, MenuListDto parentMenu, boolean flatList) {
        List<MenuListDto> menuList = new ArrayList<>(rawMenuList.stream()
                .filter(it ->
                        it.getMenuOrder().equals(parentMenu.getMainMenuOrder()) &&
                                it.getMainMenuOrder().equals(parentMenu.getSubMenuOrder()) &&
                                it.getSubMenuOrder().equals(parentMenu.getSubSubMenuOrder()))
                .toList());
        if (!menuList.isEmpty()) {
            List<MenuListDto> reportList = new ArrayList<>();
            menuList.forEach(menu -> {
                List<MenuListDto> subMenu = getSubMenu(rawMenuList, menu, flatList);
                if (!flatList) {
                    List<MenuListDto> subReportList = subMenu.stream().filter(it -> it.getMenuType().equals("report")).toList();
                    if (!subReportList.isEmpty() && reportList.stream().noneMatch(it -> it.getMenuId().equals(menu.getMenuId()))) {
                        MenuListDto reportMenu = menu.clone();
                        reportMenu.setMenuType("report");
                        reportMenu.setSubReport(subReportList);
                        reportMenu.setSubMenu(null);
                        reportList.add(reportMenu);
                    }
                    menu.setSubMenu(subMenu.stream().filter(it -> it.getMenuType().equals("module")).toList());
                } else {
                    menu.setSubMenu(subMenu);
                }
            });
            menuList.addAll(reportList);
            List<MenuListDto> modulesList = menuList.stream().filter(it -> it.getMenuType().equals("module")).toList();
            parentMenu.setSubMenu(modulesList);
            List<MenuListDto> repList = menuList.stream().filter(it -> it.getMenuType().equals("report")).toList();
            parentMenu.setSubReport(repList);
            List<MenuListDto> lnkList = menuList.stream().filter(it -> it.getMenuType().equals("link")).toList();
            parentMenu.setSubLink(lnkList);
        }
        return menuList;
    }
}
