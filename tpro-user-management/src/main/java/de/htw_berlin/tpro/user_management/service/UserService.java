package de.htw_berlin.tpro.user_management.service;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Group;
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.model.User;

public interface UserService extends Serializable {
	
	public User login(String username, String password);
	
	public User signUp(User user);
	
	public boolean userIsAuthorized(String username, String permissionName, String contextName);

	public void authorizeUser(String username, String permissionName, String contextName);
	
	public void deauthorizeUser(String username, String permissionName, String contextName);

	public void authorizeGroup(String groupName, String permissionName, String contextName);
	
	public void deauthorizeGroup(String groupName, String permissionName, String contextName);

	public List<User> getAuthorizedUsers(String permissionName, String contextName);
	
	public List<Group> getAuthorizedGroups(String permissionName, String contextName);
	
	public Context getContextByName(String contextName);
	
	public List<Permission> getPermissionsByContextName(String contextName);

	public void saveContext(Context context);
	
	public void updateContext(Context context);
	
}
