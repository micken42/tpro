package de.htw_berlin.tpro.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class PluginProviderManagement implements Serializable {
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
	private @Getter @Setter String pluginName;
	
	private @Getter @Setter Plugin currentPlugin;
	
	private @Getter List<User> currentPluginProviders;
	
	private @Getter @Setter List<User> currentFilteredPluginProviders;
	
	private @Getter List<Group> groups;
	private @Getter @Setter List<Group> selectedGroups;
	
	private @Getter List<User> users;
	private @Getter @Setter List<User> selectedUsers;
	
	@PostConstruct
	void init() {
		currentPlugin = pluginService.getPluginByName(pluginName);
		currentPluginProviders = pluginService.getAllPluginProvidersByPluginName(pluginName);
		currentFilteredPluginProviders = currentPluginProviders;
		
		users = userService.getAllUsers();
		selectedUsers = new ArrayList<User>();
		
		groups = userService.getAllGroups();
		selectedGroups = new ArrayList<Group>();
	}
	
	public String addUsersAndGroupsAsPluginProviders() {
		if (selectedUsers.size() == 0 && selectedGroups.size() == 0) {
			message.addMessage(FacesMessage.SEVERITY_ERROR, "Hinzufügen fehlgeschlagen!"
					+ " Sie müssen mind. einen Benutzer oder eine Gruppe auswählen");
			return null;
		}
		try {
			// add selected users as plugin providers
			for (User user : selectedUsers)
				pluginService.assignUserToPluginAsPluginProvider(currentPlugin.getName(), user.getUsername());
			// add selected groups as plugin providers
			for (Group group : selectedGroups) 
				pluginService.assignGroupToPluginAsPluginProviderGroup(currentPlugin.getName(), group.getName());
		} catch (Exception e) {
			message.addMessage(FacesMessage.SEVERITY_ERROR, "Hinzufügen fehlgeschlagen!"
					+ " Die Auswahl konnte nicht hinzugefügt werden :(");
		}
		
		message.addMessage(FacesMessage.SEVERITY_INFO, "Hinzufügen erfolgreich!"
				+ " Dienstanbieter wurde(n) angelegt :)");
		return navigation.goToProviderManagementPage(currentPlugin.getName());
	}
	
	public String removeUserAsPluginProvider(User provider) {
		if (currentPlugin == null || provider == null) {
			message.addMessage(FacesMessage.SEVERITY_ERROR, "Löschen fehlgeschlagen!"
					+ " Der Dienstanbieter \"" + provider.getUsername() + "\" konnte nicht gelöscht werden :(");
			return null;
		}
		// remove roles form provider
		pluginService.removeUserFromPlugin(currentPlugin.getName(), provider.getUsername());
		
		currentPluginProviders = pluginService.getAllPluginProvidersByPluginName(pluginName);
		message.addMessage(FacesMessage.SEVERITY_INFO, "Löschen erfolgreich!"
				+ " Dienstanbieter wurde(n) gelöscht :)");
		return navigation.goToProviderManagementPage(currentPlugin.getName());
	}
}