package de.htw_berlin.tpro.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.framework.DefaultPluginService;
import de.htw_berlin.tpro.framework.Plugin;
import de.htw_berlin.tpro.framework.PluginService;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class PluginProviderManagement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject @DefaultPluginService
	PluginService pluginService;

	@Inject @DefaultUserService
	UserService userService;
	
	private @Getter @Setter Plugin currentPlugin;
	
	private @Getter List<User> currentPluginProviders;
	
	private @Getter @Setter List<User> currentFilteredPluginProviders;
	
	private @Getter List<String> groups;
	private @Getter @Setter List<String> selectedGroups;
	
	private @Getter List<String> users;
	private @Getter @Setter List<String> selectedUsers;
	
	@PostConstruct
	void initPluginView() {
		String pluginName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pluginName");
		currentPlugin = pluginService.getPluginByName(pluginName);
		currentPluginProviders = pluginService.getAllPluginProvidersByPluginName(pluginName);
		currentFilteredPluginProviders = currentPluginProviders;
		
		users = userService.getAllUsernames();
		selectedUsers = new ArrayList<String>();
		
		groups = userService.getAllGroupNames();
		selectedGroups = new ArrayList<String>();
	}
	
	public void removeProvider(User provider) {
		currentPluginProviders.remove(provider);
	}
}