package com.iitm.hosteldine.controller.asset;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.asset.AssetCategoryForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.asset.AssetConfigurationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.asset.configuration}")
public class AssetConfigurationController {

	private final AssetConfigurationService assetConfigService;
	private final SimsConfigDataService simsConfigDataService;
	private final CommonResponseUtil commonResponseUtil;

	@Value("${url.asset.configuration}")
	private String getAssetConfig;

	@GetMapping
	public String getAssetConfigList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		//map.addAttribute("assetCategoryList", assetConfigService.getAssetConfigList());
		Page<AssetCategoryForm> assetCategoryList = assetConfigService.getAssetConfigList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request,assetCategoryList,form);
		return HTMLPage.ASSET_CONFIGURATION;
	}

	@GetMapping("${type}" + "${id}")
	public String getAssetConfigDetailsByCategoryId(@PathVariable String type, @PathVariable Long id, ModelMap map,
			HttpServletRequest request) throws Exception {
		AssetCategoryForm assetCategoryForm = new AssetCategoryForm();
		if (id != null && type != null && id != 0 && type != "0") {
			assetCategoryForm = assetConfigService.getAssetConfigDetailsByCategoryId(type, id);
		}
		map.addAttribute("assetCategoryForm", assetCategoryForm);
		map.addAttribute("assetConfigList", simsConfigDataService.getSimConfigValueFromJsonArray("ASSET_CONFIG_TYPES"));
		return HTMLPage.ADD_EDIT_ASSET_CONFIGURATION;
	}

	@PostMapping
	public String saveAndUpdate(ModelMap map, @ModelAttribute AssetCategoryForm assetCategoryForm,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = assetConfigService.saveAndUpdate(assetCategoryForm);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
		return Constants.REDIRECT + getAssetConfig;
	}

	@DeleteMapping("${type}" + "${id}")
	public @ResponseBody BaseResponse deleteAssetConfigDetailsByCategoryId(@PathVariable String type,
			@PathVariable Long id, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttrs)
			throws Exception {
		try {
			return CommonResponseUtil
					.generateDeleteResponseByStatus(assetConfigService.deleteAssetConfigDetailsByCategoryId(type, id));
		} catch (RecordNotExistsException e) {
			// Create a custom error response in case of an exception
			BaseResponse errorResponse = new BaseResponse();
			errorResponse.setMessage(e.getMessage());
			errorResponse.setStatus("Failure");
			return errorResponse;
		}
	}

	@GetMapping("${url.exist}")
	public @ResponseBody boolean checkAssetNameExist(@ModelAttribute AssetCategoryForm assetCategoryForm,
			ModelMap map, HttpServletRequest request) {
		return assetConfigService.checkAssetNameExist(assetCategoryForm);
	}

	@GetMapping("${url.shortcode.exist}")
	public @ResponseBody boolean checkShortnameExist(@ModelAttribute AssetCategoryForm assetCategoryForm,
			ModelMap map, HttpServletRequest request) {
		return assetConfigService.checkShortnameExist(assetCategoryForm);
	}

}
