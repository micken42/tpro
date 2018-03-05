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
	
	@Inject @DefaultPluginFinder
	PluginFinder pluginFinder;

	@PostConstruct
	void initializePlugins() {
		plugins = (HashMap<String, Plugin>) pluginFinder.findAndInititalizePlugins();
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
		List<Permission> pluginPermissions = userService.getPermissionsByContextName(plugin);
		for (Permission permission : pluginPermissions) {
			if(!userService.userIsAuthorized(username, permission.getName(), plugin))
				return false;
		}
		return true;
	}

	/**
	 * return true if user with the given username has at least one permission in the given plugin
	 */
	@Override
	public boolean userIsPluginConsumer(String username, String plugin) {
		List<Permission> pluginPermissions = userService.getPermissionsByContextName(plugin);
		for (Permission permission : pluginPermissions) {
			if(userService.userIsAuthorized(username, permission.getName(), plugin))
				return true;
		}
		return false;
	}
	
	@Override
	public List<Plugin> getPluginsProvidableByUserWithUsername(String username) {
		List<Plugin> allPlugins = getAllPlugins();
		if (allPlugins == null) 
			return null;
		List<Plugin> plugins = new ArrayList<Plugin>();
		for (Plugin plugin : allPlugins) {
			if (userIsPluginProvider(username, plugin.getName()))
				plugins.add(plugin);
		}
		return (plugins.size() != 0) ? plugins : null;
	}

	@Override
	public List<String> getNamesOfPluginsAcessableByUserWithUsername(String username) {
		List<Plugin> allPlugins = getAllPlugins();
		if (allPlugins == null) 
			return null;
		List<String> pluginNames = new ArrayList<String>();
		for (Plugin plugin : allPlugins) {
			if (userIsPluginConsumer(username, plugin.getName()))
				pluginNames.add(plugin.getName());
		}
		return (pluginNames.size() != 0) ? pluginNames : null;
	}

	@Override
	public void assignUserToPluginAsPluginProvider(String plugin, String username) {
		List<Permission> pluginPermissions = userService.getPermissionsByContextName(plugin);
		for (Permission permission : pluginPermissions) 
			userService.authorizeUser(username, permission.getName(), plugin);
	}

	@Override
	public void assignUsersToPluginAsPluginProviders(String plugin, List<String> usernames) {
		List<Permission> pluginPermissions = userService.getPermissionsByContextName(plugin);
		for (String username : usernames) 
			pluginPermissions.forEach(permission -> userService.authorizeUser(username, permission.getName(), plugin));
	}

	@Override
	public void assignGroupToPluginAsPluginProviderGroup(String plugin, String groupName) {
		List<Permission> pluginPermissions = userService.getPermissionsByContextName(plugin);
		for (Permission permission : pluginPermissions)
			userService.authorizeGroup(groupName, permission.getName(), plugin);
	}

	@Override
	public void removePluginProviderFromPlugin(String plugin, String username) {
		List<Permission> pluginPermissions = userService.getPermissionsByContextName(plugin);
		for (Permission permission : pluginPermissions) 
			userService.deauthorizeUser(username, permission.getName(), plugin);
	}

	@Override
	public void removePluginProvidersFromPlugin(String plugin, List<String> usernames) {
		List<Permission> pluginPermissions = userService.getPermissionsByContextName(plugin); 
		for (String username : usernames) 
			pluginPermissions.forEach(permission -> userService.deauthorizeUser(username, permission.getName(), plugin));
	}

	@Override
	public void removePluginProviderGroupFromPlugin(String plugin, String groupName) {
		List<Permission> pluginPermissions = userService.getPermissionsByContextName(plugin);
		for (Permission permission : pluginPermissions)
			userService.deauthorizeGroup(groupName, permission.getName(), plugin);
	}

	@Override
	public void removeAllPluginProvidersFromPlugin(String plugin) {
		// TODO Remove all ???
	}
	
}