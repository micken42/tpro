package de.htw_berlin.tpro.user_management.service;

import java.io.Serializable;

import de.htw_berlin.tpro.user_management.model.User;

public interface UserManagementService extends Serializable {
	
	public User login(String username, String password);
	
	public boolean userIsAuthorized(User user, String permission, String context);

	public User signUp(User user);
	
}
