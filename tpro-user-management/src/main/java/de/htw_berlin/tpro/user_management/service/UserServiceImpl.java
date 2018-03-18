package de.htw_berlin.tpro.user_management.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;

import org.omnifaces.cdi.Startup;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Group;
import de.htw_berlin.tpro.user_management.model.Role;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.persistence.ContextFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultContextFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultGroupFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultRoleFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultUserFacade;
import de.htw_berlin.tpro.user_management.persistence.GroupFacade;
import de.htw_berlin.tpro.user_management.persistence.RoleFacade;
import de.htw_berlin.tpro.user_management.persistence.UserFacade;

@Named("userService")
@Startup
@ApplicationScoped
@DefaultUserService 
public class UserServiceImpl implements UserService {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultUserFacade
	UserFacade userFacade;

	@Inject @DefaultRoleFacade
	RoleFacade roleFacade;

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

	/**
	 * Ein Benutzer gilt nur dann als authorisiert, wenn er die Rolle (identifizierbar 
	 * über den gegebenen Rollennamen (roleName) in Kombination mit dem übergebenen
	 * Kontextnamen (contextName)) besitzt oder Mitglied einer Gruppe ist, die die 
	 * Rolle hat.
	 */
	@Override
	public boolean userIsAuthorized(String username, String roleName, String contextName) {
		Role role = roleFacade.getRoleByRoleAndContextName(roleName, contextName);
		User user = userFacade.getUserByUsername(username);

		if (user == null || role == null)
			return false;
		
		// Check users roles
		if (user.hasRole(role)) 
			return true;
		
		// Check users group roles
		Set<Group> usersGroups = user.getGroups();
		for (Group group : usersGroups) {
			if (group.hasRole(role)) 
				return true;
		}
		return false;
	}

	@Override
	public List<User> getAuthorizedUsers(String roleName, String contextName) {
		List<User> authorizedUsers = new ArrayList<User>();
		// Getting all users with the given role
		List<User> usersWithRole = userFacade.getUsersByRoleAndContextName(roleName, contextName);
		if (usersWithRole != null)
			authorizedUsers = usersWithRole;
		// Getting all users within groups that have the given role
		List<Group> groupsWithRole = groupFacade.getGroupsByRoleAndContextName(roleName, contextName);
		for (Group group : groupsWithRole) {
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
	public void authorizeUser(String username, String roleName, String contextName) {
		Role role = roleFacade.getRoleByRoleAndContextName(roleName, contextName);
		User user = userFacade.getUserByUsername(username);
		if (user == null || role == null)
			return;
		if (!user.hasRole(role))
			user.addRole(role);
		userFacade.updateUser(user);
	}

	@Override
	public void deauthorizeUser(String username, String roleName, String contextName) {
		Role role = roleFacade.getRoleByRoleAndContextName(roleName, contextName);
		User user = userFacade.getUserByUsername(username);
		if (user == null || role == null)
			return;
		// Search in users groups for the role and delete them if found
		deleteUserFromGroupsWithRole(user, role);
		if (user.hasRole(role))
			user.removeRole(role);
		userFacade.updateUser(user);
	}

	private void deleteUserFromGroupsWithRole(User user, Role role) {
		for(Group group : user.getGroups()) {
			if (group.hasRole(role)) {
				user.removeGroup(group);
				group.removeUser(user);
				groupFacade.updateGroup(group);
			}
		}
	}

	@Override
	public List<Group> getAuthorizedGroups(String roleName, String contextName) {
		List<Group> authorizedGroups = groupFacade.getGroupsByRoleAndContextName(roleName, contextName);
		return authorizedGroups;
	}

	@Override
	public void authorizeGroup(String groupName, String roleName, String contextName) {
		Role role = roleFacade.getRoleByRoleAndContextName(roleName, contextName);
		Group group = groupFacade.getGroupByName(groupName);
		if (group == null || role == null)
			return;
		if (!group.hasRole(role))
			group.addRole(role);
		groupFacade.updateGroup(group);
	}

	@Override
	public void deauthorizeGroup(String groupName, String roleName, String contextName) {
		Role role = roleFacade.getRoleByRoleAndContextName(roleName, contextName);
		Group group = groupFacade.getGroupByName(groupName);
		if (group == null || role == null)
			return;
		if (group.hasRole(role))
			group.removeRole(role);
		groupFacade.updateGroup(group);
	}

	@Override
	public Context getContextByName(String contextName) {
		return contextFacade.getContextByName(contextName);
	}

	@Override
	public List<Role> getRolesByContextName(String contextName) {
		return roleFacade.getRolesByContextName(contextName);
	}

	@Override
	public void saveContext(Context context) {
		contextFacade.saveContext(context);
	}

	@Override
	public void updateContext(Context context) {
		contextFacade.updateContext(context);
	}

	@Override
	public List<User> getAllUsers() {
		return userFacade.getAllUsers();
	}

	@Override
	public List<Group> getAllGroups() {
		return groupFacade.getAllGroups();
	}

	@Override
	public List<String> getAllUsernames() {
		return userFacade.getAllUsernames();
	}

	@Override
	public List<String> getAllGroupNames() {
		return groupFacade.getAllNames();
	}

	@Override
	public void saveGroup(Group group) {
		groupFacade.saveGroup(group);
	}

	@Override
	public Object getGroupByName(String groupName) {
		return groupFacade.getGroupByName(groupName);
	}

	@Override
	public void deleteGroupByName(String groupName) {
		groupFacade.deleteGroupByName(groupName);
	}

	@Override
	public void deleteUserByUsername(String username) {
		userFacade.deleteUserByUsername(username);
	}
}
