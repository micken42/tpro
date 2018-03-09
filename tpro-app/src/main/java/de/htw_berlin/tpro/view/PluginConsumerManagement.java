package de.htw_berlin.tpro.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.framework.DefaultPluginService;
import de.htw_berlin.tpro.framework.Plugin;
import de.htw_berlin.tpro.framework.PluginService;
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

	@Inject @DefaultPluginService
	PluginService pluginService;

	@Inject @DefaultUserService
	UserService userService;
	
	private @Getter @Setter Plugin currentPlugin;
	
	private @Getter List<User> currentPluginConsumers;
	
	private @Getter @Setter List<User> currentFilteredPluginConsumers;
	
	private @Getter List<String> groups;
	private @Getter @Setter List<String> selectedGroups;

	private @Getter List<String> users;
	private @Getter @Setter List<String> selectedUsers;
	
	private @Getter List<String> roles;
	private @Getter @Setter List<String> selectedRoles;
	
	@PostConstruct
	void initPluginView() {
		String pluginName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pluginName");
		currentPlugin = pluginService.getPluginByName(pluginName);
		currentPluginConsumers = pluginService.getAllUsersWithAccessToPlugin(pluginName);
		currentFilteredPluginConsumers = currentPluginConsumers;
		
		users = userService.getAllUsernames();
		selectedUsers = new ArrayList<String>();

		groups = userService.getAllGroupNames();
		selectedGroups = new ArrayList<String>();
		
		roles = new ArrayList<String>();
		userService.getRolesByContextName(pluginName).forEach(p -> roles.add(p.getName()));
		selectedRoles = new ArrayList<String>();
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