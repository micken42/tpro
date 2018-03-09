package de.htw_berlin.tpro.user_management.service;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Group;
import de.htw_berlin.tpro.user_management.model.Role;
import de.htw_berlin.tpro.user_management.model.User;

public interface UserService extends Serializable {
	
	public User login(String username, String password);
	
	public User signUp(User user);
	
	public boolean userIsAuthorized(String username, String roleName, String contextName);

	public void authorizeUser(String username, String roleName, String contextName);
	
	public void deauthorizeUser(String username, String roleName, String contextName);

	public void authorizeGroup(String groupName, String roleName, String contextName);
	
	public void deauthorizeGroup(String groupName, String roleName, String contextName);

	public List<User> getAuthorizedUsers(String roleName, String contextName);
	
	public List<Group> getAuthorizedGroups(String roleName, String contextName);
	
	public Context getContextByName(String contextName);
	
	public List<Role> getRolesByContextName(String contextName);

	public void saveContext(Context context);
	
	public void updateContext(Context context);
	
	public List<User> getAllUsers();
	
	public List<Group> getAllGroups();

	public List<String> getAllUsernames();

	public List<String> getAllGroupNames();
	
}
