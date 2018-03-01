package de.htw_berlin.tpro.user_management.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.persistence.dao.ContextFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.DefaultContextFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.DefaultPermissionFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.DefaultUserFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.PermissionFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.UserFacade;

@ApplicationScoped
@DefaultUserManagement 
public class DefaultUserManagementService implements UserManagementService {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultUserFacade
	UserFacade userFacade;

	@Inject @DefaultPermissionFacade
	PermissionFacade permissionFacade;

	@Inject @DefaultContextFacade
	ContextFacade contextFacade;
	
	@Override
	public User login(String username, String password) {
		User user = userFacade.getUserByUsername(username);
		if (user != null)
			user = (user.getPassword().equals(password)) ? user : null;
		return user;
	}

	@Override
	public User signUp(User user) {
		userFacade.saveUser(user);
		user.addPermission(permissionFacade.getPermissionByPermissionAndContextName("user", "tpro"));
		userFacade.updateUser(user);
		return user;
	}

	@Override
	public boolean userIsAuthorized(User user, String permission, String context) {
		if (user == null)
			return false;
		for (Permission perm : user.getPermissions()) {
			if (perm.getContext().getName().equals(context) 
					&& perm.getName().equals(permission))
				return true;
		}
		return false;
	}
}
