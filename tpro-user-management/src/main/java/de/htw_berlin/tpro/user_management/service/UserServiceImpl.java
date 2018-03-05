package de.htw_berlin.tpro.user_management.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import de.htw_berlin.tpro.user_management.model.Group;
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.persistence.ContextFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultContextFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultGroupFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultPermissionFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultUserFacade;
import de.htw_berlin.tpro.user_management.persistence.GroupFacade;
import de.htw_berlin.tpro.user_management.persistence.PermissionFacade;
import de.htw_berlin.tpro.user_management.persistence.UserFacade;

@ApplicationScoped
@DefaultUserService 
public class UserServiceImpl implements UserService {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultUserFacade
	UserFacade userFacade;

	@Inject @DefaultPermissionFacade
	PermissionFacade permissionFacade;

	@Inject @DefaultContextFacade
	ContextFacade contextFacade;

	@Inject @DefaultGroupFacade
	GroupFacade groupFacade;
	
	@Override
	public User login(String username, String password) {
		User user = userFacade.getUserByUsername(username);
		if (user != null)
			user = (user.getPassword().equals(password)) ? user : null;
		return user;
	}

	@Override
	public User signUp(User user) throws PersistenceException {
		userFacade.saveUser(user);	
		return user;
	}

	@Override
	public boolean userIsAuthorized(String username, String permissionName, String contextName) {
		Permission permission = permissionFacade.getPermissionByPermissionAndContextName(permissionName, contextName);
		User user = userFacade.getUserByUsername(username);
		if (user == null || permission == null)
			return false;
		
		// Check users permissions
		if (user.hasPermission(permission)) 
			return true;
		
		// Check users group permissions
		List<Group> usersGroups = groupFacade.getGroupsByUsername(username);
		for (Group group : usersGroups) {
			if (group.hasPermission(permission)) 
				return true;
		}
		return false;
	}

	@Override
	public List<User> getAuthorizedUsers(String permissionName, String contextName) {
		List<User> authorizedUsers = new ArrayList<User>();
		// Getting all users with the given permission
		List<User> usersWithPermission = userFacade.getUsersByPermissionAndContextName(permissionName, contextName);
		if (usersWithPermission != null)
			authorizedUsers = usersWithPermission;
		// Getting all users within groups that have the given permission
		List<Group> groupsWithPermission = groupFacade.getGroupsByPermissionAndContextName(permissionName, contextName);
		for (Group group : groupsWithPermission) {
			addNewUserFromGroupToUserList(authorizedUsers, group);
		}
		return authorizedUsers;
	}

	private void addNewUserFromGroupToUserList(List<User> userList, Group group) {
		for (User user : group.getUsers()) {
			boolean duplicateUser = false;
			for (User listUser : userList) {
				if (listUser.getUsername().equals(user.getUsername())) 
					duplicateUser = true;
			}
			if (!duplicateUser)
				userList.add(user);
		}
	}

	@Override
	public void authorizeUser(String username, String permissionName, String contextName) {
		Permission permission = permissionFacade.getPermissionByPermissionAndContextName(permissionName, contextName);
		User user = userFacade.getUserByUsername(username);
		if (user.hasPermission(permission) || user == null || permission == null)
			return;
		user.addPermission(permission);
		userFacade.updateUser(user);
	}

	@Override
	public void deauthorizeUser(String username, String permissionName, String contextName) {
		Permission permission = permissionFacade.getPermissionByPermissionAndContextName(permissionName, contextName);
		User user = userFacade.getUserByUsername(username);
		if (user == null || permission == null)
			return;
		// Search in users groups for the permission and delete them if found
		deleteUserFromGroupsWithPermission(user, permission);
		if (user.hasPermission(permission))
			user.removePermission(permission);
		userFacade.updateUser(user);
	}

	private void deleteUserFromGroupsWithPermission(User user, Permission permission) {
		for(Group group : user.getGroups()) {
			if (group.hasPermission(permission)) {
				user.removeGroup(group);
				group.removeUser(user);
				groupFacade.updateGroup(group);
			}
		}
	}

	@Override
	public List<Group> getAuthorizedGroups(String permissionName, String contextName) {
		List<Group> authorizedGroups = groupFacade.getGroupsByPermissionAndContextName(permissionName, contextName);
		return authorizedGroups;
	}

	@Override
	public void authorizeGroup(String groupName, String permissionName, String contextName) {
		Permission permission = permissionFacade.getPermissionByPermissionAndContextName(permissionName, contextName);
		Group group = groupFacade.getGroupByName(groupName);
		if (group == null || permission == null)
			return;
		if (!group.hasPermission(permission))
			group.addPermission(permission);
		groupFacade.updateGroup(group);
	}

	@Override
	public void deauthorizeGroup(String groupName, String permissionName, String contextName) {
		Permission permission = permissionFacade.getPermissionByPermissionAndContextName(permissionName, contextName);
		Group group = groupFacade.getGroupByName(groupName);
		if (group == null || permission == null)
			return;
		if (group.hasPermission(permission))
			group.removePermission(permission);
		groupFacade.updateGroup(group);
	}
}
