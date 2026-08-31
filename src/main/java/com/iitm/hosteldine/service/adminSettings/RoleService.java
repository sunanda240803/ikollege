package com.iitm.hosteldine.service.adminSettings;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.RoleDto;
import com.iitm.hosteldine.entity.RoleEntity;
import com.iitm.hosteldine.exception.InternalErrorException;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.mapper.RoleMapper;
import com.iitm.hosteldine.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final MessageSource messageSource;

    public ArrayList<RoleDto> getRoleList() {
        ArrayList<RoleDto> resultList = null;
        Optional<List<RoleEntity>> rolesList = roleRepository.findAllByActiveFlagOrderByRoleName(ModelConstants.STATUS_ACTIVE);
        if (rolesList.isPresent()) {
            resultList = new ArrayList<>();
            for (RoleEntity role : rolesList.get()) {
                RoleDto RoleDto = RoleMapper.INSTANCE.toRoleDTO(role);
                resultList.add(RoleDto);
            }
        }
        return resultList;
    }

    public RoleDto getRoleById(Long id) {
        Optional<RoleEntity> roleResult = roleRepository.findByRoleIdAndActiveFlag(id,
                ModelConstants.STATUS_ACTIVE);
        RoleDto result = roleResult.map(RoleMapper.INSTANCE::toRoleDTO).orElse(null);
        return result;
    }

    public RoleDto getRoleIdByName(String roleName) throws RecordNotExistsException {
        Optional<RoleEntity> roleResult = roleRepository.findByRoleNameAndActiveFlag(roleName,
                ModelConstants.STATUS_ACTIVE);
        RoleDto result = new RoleDto();
        if (roleResult.isPresent()) {
            result = roleResult.map(RoleMapper.INSTANCE::toRoleDTO).orElse(null);
        } else {
            throw new RecordNotExistsException(messageSource.getMessage("validation.error.role.id.not.found", null, Locale.getDefault()));
        }
        return result;
    }

    public RoleDto saveUpdateRole(RoleDto RoleDto) {
        RoleEntity roles = RoleMapper.INSTANCE.toRoleEntity(RoleDto);
        if (roles.getRoleId() > 0) {
            roles.onCreate();
        }
        RoleEntity afterSave = roleRepository.saveAndFlush(roles);
        RoleDto result = null;
        if (afterSave.equals(roles)) {
            result = RoleMapper.INSTANCE.toRoleDTO(roles);
        }
        return result;
    }

    public boolean deleteRole(Long id) {
        Optional<RoleEntity> roles = roleRepository.findById(id);
        boolean result = false;
        if (roles.isPresent()) {
            RoleEntity role = roles.get();
            role.setActiveFlag(ModelConstants.STATUS_INACTIVE);
            role = roleRepository.saveAndFlush(role);
            result = role.getActiveFlag().equals(ModelConstants.STATUS_INACTIVE);
        }
        return result;
    }

    public void checkForNewRole(RoleDto role) throws InternalErrorException {
        if (role == null) {
            throw new InternalErrorException("Role information is empty.");
        }
        if (role.getRoleId() == null || role.getRoleId() == 0) {
            Optional<RoleEntity> optionalRole = roleRepository.findByRoleName(role.getRoleName());
            if (optionalRole.isEmpty()) {
                RoleEntity newRoleEntity = RoleMapper.INSTANCE.toRoleEntity(role);
                newRoleEntity.onCreate();
                try {
                    roleRepository.saveAndFlush(newRoleEntity);
                } catch (DataIntegrityViolationException exception) {
                    if (exception.getMessage().contains("duplicate key value")) {
                        checkForNewRole(role);
                    }
                }
                if (newRoleEntity.getRoleId() != null && newRoleEntity.getRoleId() > 0) {
                    role.setRoleId(newRoleEntity.getRoleId());
                }
            } else {
                role.setRoleId(optionalRole.get().getRoleId());
            }
        }
    }
}
