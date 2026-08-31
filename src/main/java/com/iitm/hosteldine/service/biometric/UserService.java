package com.iitm.hosteldine.service.biometric;

import com.iitm.hosteldine.dao.biometric.UserDao;
import com.iitm.hosteldine.model.biometric.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
@Slf4j
public class UserService {


    UserDao userDao;
	
	/*@Override
	public DostRole registerUser(DostRole user) {
		return userDao.registerUser(user);
	}*/

    public User getUserByEmail(String username) {
        log.info("Getting user by username{}", username);
        return userDao.findByUserName(username);
    }

//	@Override
//	public List<Role> getRoleByMenu(String login) {
//		return userDao.getRoleByMenu(login);
//	}

//	@Override
//	public List<MenuRoleUserOrderView> getMenuByRole(String login) {
//		return userDao.getMenuByRole(login);
//	}

    public String getFPStatusById(String id) {
        return userDao.getFPStatusById(id);
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
