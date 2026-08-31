package com.iitm.hosteldine.service.asset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.asset.AssetCategoryForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.asset.AssetCategoryInfoMapper;
import com.iitm.hosteldine.mapper.asset.AssetMaintenanceTypeMapper;
import com.iitm.hosteldine.model.asset.AssetCategoryInfoEntity;
import com.iitm.hosteldine.model.asset.AssetMaintenanceTypeEntity;
import com.iitm.hosteldine.repository.asset.AssetCategoryInfoRepository;
import com.iitm.hosteldine.repository.asset.AssetInventoryInfoRepository;
import com.iitm.hosteldine.repository.asset.AssetMaintenanceTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetConfigurationService {
	private final AssetCategoryInfoRepository categoryInfoRepo;
	private final AssetMaintenanceTypeRepository maintenanceTypeRepo;
	private final AssetInventoryInfoRepository assetInventoryRepo;
	private final MessageSource messageSource;

	public ArrayList<AssetCategoryForm> getCategoryList() {
		ArrayList<AssetCategoryForm> assetCategoryFormList = new ArrayList<>();
		List<AssetCategoryInfoEntity> categoryList = categoryInfoRepo
				.findAllByActiveFlagOrderByAssetCategory(ModelConstants.STATUS_ACTIVE);
		if (!categoryList.isEmpty()) {
			categoryList.forEach(category -> {
				AssetCategoryForm assetCategoryForm = AssetCategoryInfoMapper.INSTANCE
						.fromAssetCategoryInfoEntityToForm(category);
				assetCategoryFormList.add(assetCategoryForm);
			});
		}
		return assetCategoryFormList;
	}

	public ArrayList<AssetCategoryForm> getMaintenanceTypeList() {
		ArrayList<AssetCategoryForm> assetCategoryFormList = new ArrayList<>();
		List<AssetMaintenanceTypeEntity> maintenanceTypeList = maintenanceTypeRepo
				.findAllByActiveFlagOrderByMaintenanceType(ModelConstants.STATUS_ACTIVE);
		if (!maintenanceTypeList.isEmpty()) {
			maintenanceTypeList.forEach(maintenanceType -> {
				AssetCategoryForm assetCategoryForm = AssetMaintenanceTypeMapper.INSTANCE
						.fromAssetMaintenanceTypeEntityToForm(maintenanceType);
				assetCategoryFormList.add(assetCategoryForm);
			});
		}
		return assetCategoryFormList;
	}

	public List<AssetCategoryForm> getAssetConfigList() {
		ArrayList<AssetCategoryForm> assetCategoryFormList = new ArrayList<>();
		assetCategoryFormList.addAll(getCategoryList());
		assetCategoryFormList.addAll(getMaintenanceTypeList());
		if (!assetCategoryFormList.isEmpty()) {
			assetCategoryFormList.sort(Comparator.comparing(AssetCategoryForm::getModifiedAt).reversed());
		}
		return assetCategoryFormList;
	}

	public AssetCategoryForm getAssetConfigDetailsByCategoryId(String type, Long id) {
		return switch (type) {
		case ModelConstants.ASSET_CATEGORY ->
			categoryInfoRepo.findByAssetCategoryIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE).map(entity -> {
				AssetCategoryForm form = AssetCategoryInfoMapper.INSTANCE.fromAssetCategoryInfoEntityToForm(entity);

				// Format costs to strings with two decimal places
				if (entity.getMinorRepairCost() != null) {
					BigDecimal minorRepairCost = BigDecimal.valueOf(entity.getMinorRepairCost()).setScale(2,
							RoundingMode.HALF_UP); // Set scale to 2 for two decimal places
					form.setMinorCostString(minorRepairCost.toPlainString());
				}
				if (entity.getMajorRepairCost() != null) {
					BigDecimal majorRepairCost = BigDecimal.valueOf(entity.getMajorRepairCost()).setScale(2,
							RoundingMode.HALF_UP); 
					form.setMajorCostString(majorRepairCost.toPlainString());
				}
				if (entity.getReplacementCost() != null) {
					BigDecimal replacementCost = BigDecimal.valueOf(entity.getReplacementCost()).setScale(2,
							RoundingMode.HALF_UP);
					form.setReplacementCostString(replacementCost.toPlainString());
				}
				return form;
			}).orElse(new AssetCategoryForm());
		case ModelConstants.ASSET_MAINTENANCE_TYPE ->
			maintenanceTypeRepo.findByMaintenanceTypeIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
					.map(AssetMaintenanceTypeMapper.INSTANCE::fromAssetMaintenanceTypeEntityToForm)
					.orElse(new AssetCategoryForm());
		default -> new AssetCategoryForm();
		};
	}

	@Transactional
	public String saveAndUpdate(AssetCategoryForm assetCategoryForm) throws RecordNotExistsException {
		String isSaved = null;
		boolean isUpdate = assetCategoryForm.getId() > 0;
		boolean isAssetCategory = assetCategoryForm.getCategoryType().equals(ModelConstants.ASSET_CATEGORY);
		String returnStatus = isUpdate ? "update" : "save";

		if (isAssetCategory) {
			isSaved = saveOrUpdateCategory(assetCategoryForm, isUpdate, returnStatus);
		} else {
			isSaved = saveOrUpdateMaintenanceType(assetCategoryForm, isUpdate, returnStatus);
		}
		return isSaved;
	}

	private String saveOrUpdateCategory(AssetCategoryForm assetCategoryForm, boolean isUpdate, String returnStatus)
			throws RecordNotExistsException {
		String result = null;
		AssetCategoryInfoEntity assetCategoryInfoEntity = isUpdate ? getCategoryEntity(assetCategoryForm.getId())
				: new AssetCategoryInfoEntity();
		AssetCategoryInfoMapper.INSTANCE.toAssetCategoryInfoEntity(assetCategoryInfoEntity, assetCategoryForm);

		if (isUpdate) {
			assetCategoryInfoEntity.onUpdate();
		} else {
			assetCategoryInfoEntity.setAssetCategoryId(null);
			assetCategoryInfoEntity.onCreate();
		}
		
		AssetCategoryInfoEntity entity = categoryInfoRepo.saveAndFlush(assetCategoryInfoEntity);
		if (Objects.nonNull(entity)) {
			result = "saved";
		}
		return result;
	}

	private String saveOrUpdateMaintenanceType(AssetCategoryForm assetCategoryForm, boolean isUpdate,
			String returnStatus) throws RecordNotExistsException {
		String result = null;
		AssetMaintenanceTypeEntity assetMaintenanceTypeEntity = isUpdate
				? getMaintenanceTypeEntity(assetCategoryForm.getId())
				: new AssetMaintenanceTypeEntity();
		AssetMaintenanceTypeMapper.INSTANCE.toAssetMaintenanceTypeEntity(assetMaintenanceTypeEntity, assetCategoryForm);

		if (isUpdate) {
			assetMaintenanceTypeEntity.onUpdate();
		} else {
			assetMaintenanceTypeEntity.setMaintenanceTypeId(null);
			assetMaintenanceTypeEntity.onCreate();
		}
		
		AssetMaintenanceTypeEntity entity = maintenanceTypeRepo.saveAndFlush(assetMaintenanceTypeEntity);
		if (Objects.nonNull(entity)) {
			result = "saved";
		}
		return result;
	}

	private AssetCategoryInfoEntity getCategoryEntity(Long id) throws RecordNotExistsException {
		return categoryInfoRepo.findByAssetCategoryIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.orElseThrow(() -> new RecordNotExistsException(
						messageSource.getMessage("category.type.is.not.found", null, Locale.getDefault())));
	}

	private AssetMaintenanceTypeEntity getMaintenanceTypeEntity(Long id) throws RecordNotExistsException {
		return maintenanceTypeRepo.findByMaintenanceTypeIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.orElseThrow(() -> new RecordNotExistsException(
						messageSource.getMessage("maintenance.type.is.not.found", null, Locale.getDefault())));
	}

	public Boolean deleteAssetConfigDetailsByCategoryId(String type, Long id)
			throws NoSuchMessageException, RecordNotExistsException {
		if (assetInventoryRepo.existsByActiveFlagAndAssetCategoryId(ModelConstants.STATUS_ACTIVE, id)) {
			throw new RecordNotExistsException(
					messageSource.getMessage("message.validation.error.asset.category.assigned", null, Locale.getDefault()));
		}
		if (type.equals(ModelConstants.ASSET_CATEGORY)) {
			AssetCategoryInfoEntity assetCategoryInfoEntity = categoryInfoRepo
					.findByAssetCategoryIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
					.orElseThrow(() -> new RecordNotExistsException(
							messageSource.getMessage("category.type.is.not.found", null, Locale.getDefault())));
			assetCategoryInfoEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			assetCategoryInfoEntity.onUpdate();
			categoryInfoRepo.saveAndFlush(assetCategoryInfoEntity);
			return true;
		} else if (type.equals(ModelConstants.ASSET_MAINTENANCE_TYPE)) {
			AssetMaintenanceTypeEntity assetMaintenanceTypeEntity = maintenanceTypeRepo
					.findByMaintenanceTypeIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
					.orElseThrow(() -> new RecordNotExistsException(
							messageSource.getMessage("maintenance.type.is.not.found", null, Locale.getDefault())));
			assetMaintenanceTypeEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			assetMaintenanceTypeEntity.onUpdate();
			maintenanceTypeRepo.saveAndFlush(assetMaintenanceTypeEntity);
			return true;
		} else {
			throw new RecordNotExistsException(
					messageSource.getMessage("given.type.is.not.valid", null, Locale.getDefault()));
		}
	}

	public Boolean checkAssetNameExist(AssetCategoryForm assetCategoryForm) {
		boolean result = false;
		Optional<AssetCategoryInfoEntity> category = Optional.empty();
		Optional<AssetMaintenanceTypeEntity> maintenance = Optional.empty();
		if (assetCategoryForm.getCategoryType().equals(ModelConstants.ASSET_CATEGORY)) {
			category = categoryInfoRepo.findByActiveFlagAndAssetCategoryIgnoreCaseAndAssetCategoryIdNot(
					ModelConstants.STATUS_ACTIVE, assetCategoryForm.getCategoryName(), assetCategoryForm.getId());
		} else {
			maintenance = maintenanceTypeRepo.findByActiveFlagAndMaintenanceTypeIgnoreCaseAndMaintenanceTypeIdNot(
					ModelConstants.STATUS_ACTIVE, assetCategoryForm.getCategoryName(), assetCategoryForm.getId());
		}
		if (Objects.nonNull(category) && category.isPresent()) {
			result = true;
		} else if (Objects.nonNull(maintenance) && maintenance.isPresent()) {
			result = true;
		}
		return result;
	}

	public boolean checkShortnameExist(AssetCategoryForm assetCategoryForm) {
		boolean result = false;
		Optional<AssetCategoryInfoEntity> category = Optional.empty();
		if (assetCategoryForm.getAssetCategoryShortcode() != null) {
			category = categoryInfoRepo.findByActiveFlagAndAssetCategoryShortcodeIgnoreCaseAndAssetCategoryIdNot(
					ModelConstants.STATUS_ACTIVE, assetCategoryForm.getAssetCategoryShortcode(),
					assetCategoryForm.getId());
		}
		if (Objects.nonNull(category) && category.isPresent()) {
			result = true;
		}
		return result;
	}

	public Page<AssetCategoryForm> getAssetConfigList(PaginationForm form) {
		List<AssetCategoryForm> assetCategoryFormList = new ArrayList<>();
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			assetCategoryFormList.addAll(getCategoryList());
			assetCategoryFormList.addAll(getMaintenanceTypeList());
		} else {
			assetCategoryFormList.addAll(getCategoryListSearch(form));
			assetCategoryFormList.addAll(getMaintenanceTypeListSearch(form));
		}
		if (!assetCategoryFormList.isEmpty()) {
			assetCategoryFormList.sort(Comparator.comparing(AssetCategoryForm::getModifiedAt).reversed());
		}
		int page = form.getPage() - 1;
		int size = form.getSize();
		Pageable pageable = PageRequest.of(page, size);
		int totalElements = assetCategoryFormList.size();
		if (totalElements == 0) {
	        return new PageImpl<>(null, pageable, totalElements);
	    }
		int start = Math.min(page * size, totalElements);
		int end = Math.min(start + size, totalElements);
		List<AssetCategoryForm> paginatedList = assetCategoryFormList.subList(start, end);

		return new PageImpl<>(paginatedList, pageable, totalElements);
	}
	
	

	public ArrayList<AssetCategoryForm> getCategoryListSearch(PaginationForm form) {
		ArrayList<AssetCategoryForm> assetCategoryFormList = new ArrayList<>();
		List<AssetCategoryInfoEntity> categoryList = categoryInfoRepo
				.findAllByAssetCategorySearch(ModelConstants.STATUS_ACTIVE,form.getSearch());
		if (!categoryList.isEmpty()) {
			categoryList.forEach(category -> {
				AssetCategoryForm assetCategoryForm = AssetCategoryInfoMapper.INSTANCE
						.fromAssetCategoryInfoEntityToForm(category);
				assetCategoryFormList.add(assetCategoryForm);
			});
		}
		return assetCategoryFormList;
	}
	
	public ArrayList<AssetCategoryForm> getMaintenanceTypeListSearch(PaginationForm form) {
		ArrayList<AssetCategoryForm> assetCategoryFormList = new ArrayList<>();
		List<AssetMaintenanceTypeEntity> maintenanceTypeList = maintenanceTypeRepo
				.findByMaintenanceTypeSearch(ModelConstants.STATUS_ACTIVE,form.getSearch());
		if (!maintenanceTypeList.isEmpty()) {
			maintenanceTypeList.forEach(maintenanceType -> {
				AssetCategoryForm assetCategoryForm = AssetMaintenanceTypeMapper.INSTANCE
						.fromAssetMaintenanceTypeEntityToForm(maintenanceType);
				assetCategoryFormList.add(assetCategoryForm);
			});
		}
		return assetCategoryFormList;
	}

}
