package de.htw_berlin.tpro.view;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.framework.DefaultPluginService;
import de.htw_berlin.tpro.framework.Plugin;
import de.htw_berlin.tpro.framework.PluginService;
import de.htw_berlin.tpro.user_management.annotation.LoggedIn;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class PluginManagement implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject @DefaultPluginService
	PluginService pluginService;
	
	@Inject @DefaultUserService
	UserService userService;
	
	@Inject @LoggedIn
	User currentUser;
	
	private @Getter @Setter Plugin currentPlugin;
	private @Getter @Setter List<String>  selectedUsers;
	private @Getter @Setter List<String> selectedGroups;
	private @Getter @Setter List<String> selectedRoles;
	private @Getter @Setter User selectedProvider;
	private @Getter @Setter User selectedConsumer;
	
	public String addUsersAndGroupsAsPluginProviders() {
		if (currentPlugin == null || (selectedUsers == null && selectedGroups == null)) 
			return null;
		// add selected users as plugin providers
		pluginService.assignUsersToPluginAsPluginProviders(currentPlugin.getName(), selectedUsers);
		// add selected groups as plugin providers
		for (String groupName : selectedGroups) {
			pluginService.assignGroupToPluginAsPluginProviderGroup(currentPlugin.getName(), groupName);
		}
		return "/admin/plugin-provider-management?faces-redirect=true&pluginName=" + currentPlugin.getName();
	}
	
	public String addUsersAndGroupsAsPluginConsumers() {
		if (currentPlugin == null || (selectedUsers == null && selectedGroups == null)  || selectedRoles == null) 
			return null;
		for (String selectedRole : selectedRoles) {
			// add selected users as plugin consumers with the selected role
			selectedUsers.forEach(username -> userService.authorizeUser(username, selectedRole, currentPlugin.getName()));
			// add selected groups as plugin consumers with the selected role
			selectedGroups.forEach(groupName -> userService.authorizeGroup(groupName, selectedRole, currentPlugin.getName()));
		}
		return "/share-plugins?faces-redirect=true&pluginName=" + currentPlugin.getName();
	}
	
	public String removeUserAsPluginProvider() {
		if (currentPlugin == null || selectedProvider == null) 
			return null;
		// remove roles form provider
		pluginService.removeUserFromPlugin(currentPlugin.getName(), selectedProvider.getUsername());
		return "/admin/plugin-provider-management?faces-redirect=true&pluginName=" + currentPlugin.getName();
	}
	
	public String removeUserAsPluginConsumer() {
		if (currentPlugin == null || selectedConsumer == null) 
			return null;
		// remove roles form provider
		pluginService.removeUserFromPlugin(currentPlugin.getName(), selectedConsumer.getUsername());
		return "/share-plugins?faces-redirect=true&pluginName=" + currentPlugin.getName();
	}
}
