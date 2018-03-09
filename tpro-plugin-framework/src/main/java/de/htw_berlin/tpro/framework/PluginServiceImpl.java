package de.htw_berlin.tpro.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.Startup;

import de.htw_berlin.tpro.user_management.model.Role;
import de.htw_berlin.tpro.user_management.model.User;
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
	public Plugin getPluginByName(String pluginName) {
		return plugins.get(pluginName);
	}

	/**
	 * return true if user with the given username has all roles in the given plugin
	 */
	@Override
	public boolean userIsPluginProvider(String username, String pluginName) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return false;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles) {
			if(!userService.userIsAuthorized(username, role.getName(), pluginName)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * return true if user with the given username has at least one role in the given plugin
	 */
	@Override
	public boolean userIsPluginConsumer(String username, String pluginName) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return false;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles) {
			if(userService.userIsAuthorized(username, role.getName(), pluginName))
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
	public List<Plugin> getPluginsAccessableByUserWithUsername(String username) {
		List<Plugin> allPlugins = getAllPlugins();
		if (allPlugins == null) 
			return null;
		List<Plugin> plugins = new ArrayList<Plugin>();
		for (Plugin plugin : allPlugins) {
			if (userIsPluginConsumer(username, plugin.getName()))
				plugins.add(plugin);
		}
		return (plugins.size() != 0) ? plugins : null;
	}

	@Override
	public void assignUserToPluginAsPluginProvider(String pluginName, String username) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles) 
			userService.authorizeUser(username, role.getName(), pluginName);
	}

	@Override
	public void assignUsersToPluginAsPluginProviders(String pluginName, List<String> usernames) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (String username : usernames) 
			pluginRoles.forEach(role -> userService.authorizeUser(username, role.getName(), pluginName));
	}

	@Override
	public void assignGroupToPluginAsPluginProviderGroup(String pluginName, String groupName) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles)
			userService.authorizeGroup(groupName, role.getName(), pluginName);
	}

	@Override
	public void removeUserFromPlugin(String pluginName, String username) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles) 
			userService.deauthorizeUser(username, role.getName(), pluginName);
	}

	@Override
	public void removeUsersFromPlugin(String pluginName, List<String> usernames) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (String username : usernames) 
			pluginRoles.forEach(role -> userService.deauthorizeUser(username, role.getName(), pluginName));
	}

	@Override
	public void removeGroupsFromPlugin(String pluginName, String groupName) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles)
			userService.deauthorizeGroup(groupName, role.getName(), pluginName);
	}

	@Override
	public List<User> getAllPluginProvidersByPluginName(String pluginName) {
		List<User> providers = new ArrayList<User>();
		for (User user : userService.getAllUsers()) {
			if (userIsPluginProvider(user.getUsername(), pluginName))
				providers.add(user);
		}
		return (providers.size() != 0) ? providers : null;
	}

	@Override
	public List<User> getAllUsersWithAccessToPlugin(String pluginName) {
		List<User> consumers = new ArrayList<User>();
		for (User user : userService.getAllUsers()) {
			if (userIsPluginConsumer(user.getUsername(), pluginName))
				consumers.add(user);
		}
		return (consumers.size() != 0) ? consumers : null;
	}
	
}