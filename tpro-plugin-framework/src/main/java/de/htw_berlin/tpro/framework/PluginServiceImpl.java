package de.htw_berlin.tpro.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.Startup;

import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.persistence.DefaultPermissionFacade;
import de.htw_berlin.tpro.user_management.persistence.PermissionFacade;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;

@Named(value="pluginService")
@Startup
@ApplicationScoped
@DefaultPluginService
public class PluginServiceImpl implements PluginService
{
	private static final long serialVersionUID = 1L;

	private HashMap<String, Plugin> plugins;
	
	@Inject @DefaultUserService
	UserService userService; 
	
	@Inject @DefaultPermissionFacade
	PermissionFacade permissionFacade;
	
	@Inject 
	PluginFinder pluginFinder;

	@PostConstruct
	void initializePlugins() {
		this.plugins = pluginFinder.getPlugins();
	}

	@Override
	public List<Plugin> getAllPlugins() {
		List<Plugin> result = new ArrayList<Plugin>();
		for (Object plugin : plugins.values().toArray()) 
			result.add((Plugin) plugin);
		return result;
	}

	@Override
	public Plugin getPluginByName(String plugin) {
		return plugins.get(plugin);
	}

	/**
	 * return true if user with the given username has all permissions in the given plugin
	 */
	@Override
	public boolean userIsPluginProvider(String username, String plugin) {
		List<Permission> pluginPermissions = permissionFacade.getPermissionsByContextName(plugin);
		for (Permission permission : pluginPermissions) {
			if(!userService.userIsAuthorized(username, permission.getName(), permission.getContext().getName()))
				return false;
		}
		return true;
	}
	
	@Override
	public List<Plugin> getPluginsProvidableByUserWithUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getNamesOfPluginsAcessableByUserWithUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void assignUserToPluginAsPluginProvider(String plugin, String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void assignUsersToPluginAsPluginProviders(String plugin, List<String> usernames) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void assignGroupToPluginAsPluginProviderGroup(String plugin, String groupName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePluginProviderFromPlugin(String plugin, String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePluginProvidersFromPlugin(String plugin, List<String> usernames) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePluginProviderGroupFromPlugin(String plugin, String groupName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllPluginProvidersFromPlugin(String plugin) {
		// TODO Auto-generated method stub
		
	}
	
}