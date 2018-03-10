package de.htw_berlin.tpro.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.Param;

import de.htw_berlin.tpro.controller.Navigation;
import de.htw_berlin.tpro.framework.DefaultPluginService;
import de.htw_berlin.tpro.framework.Plugin;
import de.htw_berlin.tpro.framework.PluginService;
import de.htw_berlin.tpro.user_management.model.Group;
import de.htw_berlin.tpro.user_management.model.Role;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class PluginConsumerManagement implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject 
	Message message;
	
	@Inject
	Navigation navigation;

	@Inject @DefaultPluginService
	PluginService pluginService;

	@Inject @DefaultUserService
	UserService userService;
	
	@Inject @Param
	@Getter @Setter String pluginName;
	
	private @Getter @Setter Plugin currentPlugin;
	
	private @Getter List<User> currentPluginConsumers;
	
	private @Getter @Setter List<User> currentFilteredPluginConsumers;
	
	private @Getter List<Group> groups;
	private @Getter @Setter List<Group> selectedGroups;

	private @Getter List<User> users;
	private @Getter @Setter List<User> selectedUsers;
	
	private @Getter List<Role> roles;
	private @Getter @Setter List<Role> selectedRoles;
	
	@PostConstruct
	void init() {
		currentPlugin = pluginService.getPluginByName(pluginName);
		currentPluginConsumers = pluginService.getAllUsersWithAccessToPlugin(pluginName);
		currentFilteredPluginConsumers = currentPluginConsumers;
		
		users = userService.getAllUsers();
		selectedUsers = new ArrayList<User>();

		groups = userService.getAllGroups();
		selectedGroups = new ArrayList<Group>();
		
		roles = new ArrayList<Role>();
		userService.getRolesByContextName(pluginName).forEach(p -> roles.add(p));
		selectedRoles = new ArrayList<Role>();
	}
	
	public String addUsersAndGroupsAsPluginConsumers() {
		if (currentPlugin == null || (selectedUsers == null && selectedGroups == null)  || selectedRoles == null) {
			message.addMessage(FacesMessage.SEVERITY_ERROR, "Hinzufügen fehlgeschlagen!"
					+ " Sie müssen mind. einen Benutzer oder eine Gruppe auswählen ;)");
			return null;
		}
		try {
			for (Role selectedRole : selectedRoles) {
				// add selected users as plugin consumers with the selected role
				selectedUsers.forEach(user -> userService.authorizeUser(user.getUsername(), selectedRole.getName(), currentPlugin.getName()));
				// add selected groups as plugin consumers with the selected role
				selectedGroups.forEach(group -> userService.authorizeGroup(group.getName(), selectedRole.getName(), currentPlugin.getName()));
			}
		} catch (Exception e) {
			message.addMessage(FacesMessage.SEVERITY_ERROR, "Hinzufügen fehlgeschlagen!"
					+ " Die Auswahl konnte nicht hinzugefügt werden :(");
		}
		message.addMessage(FacesMessage.SEVERITY_INFO, "Hinzufügen erfolgreich!"
				+ " Dienstkonsument(en) wurde(n) angelegt :)");
		return navigation.goToSharePluginPage(currentPlugin.getName());
	}
	
	public String removeUserAsPluginConsumer(User consumer) {
		if (currentPlugin == null || consumer == null) {
			message.addMessage(FacesMessage.SEVERITY_ERROR, "Löschen fehlgeschlagen!"
					+ " Der Dienstkonusment \"" + consumer.getUsername() + "\" konnte nicht gelöscht werden :(");
			return null;
		}
		// remove roles form provider
		pluginService.removeUserFromPlugin(currentPlugin.getName(), consumer.getUsername());
		message.addMessage(FacesMessage.SEVERITY_INFO, "Löschen erfolgreich!"
				+ " Dienstkonsument(en) wurde(n) gelöscht :)");
		return navigation.goToSharePluginPage(currentPlugin.getName());
	}
	
	public String printConsumerRoles(String username) {
		Set<Role> roles = currentPlugin.getRoles();
		String result = null;
		for (Role role : roles) {
			if (userService.userIsAuthorized(username, role.getName(), currentPlugin.getName())) {
				if (result == null){
					result = role.getName();
					continue;
				}
				result += ", " + role.getName();
			}
		}
		return result;
	}
	
}