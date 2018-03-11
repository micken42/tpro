package de.htw_berlin.tpro.framework;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.user_management.model.User;

public interface PluginService extends Serializable {
	
	List<Plugin> getAllPlugins();
	
	Plugin getPluginByName(String pluginName);
	
	List<Plugin> getPluginsProvidableByUserWithUsername(String username);

	List<Plugin> getPluginsAccessableByUserWithUsername(String username);
	
	boolean userIsPluginProvider(String username, String pluginName); 

	boolean userIsPluginConsumer(String username, String pluginName); 
	
	void assignUserToPluginAsPluginProvider(String pluginName, String username);
	
	void assignUsersToPluginAsPluginProviders(String pluginName, List<String> usernames);

	void assignGroupToPluginAsPluginProviderGroup(String pluginName, String groupName);
	
	void removeUserFromPlugin(String pluginName, String username);
	
	void removeUsersFromPlugin(String pluginName, List<String> usernames);
	
	void removeGroupsFromPlugin(String pluginName, String groupName);
		
	List<User> getAllPluginProvidersByPluginName(String pluginName);

	List<User> getAllUsersWithAccessToPlugin(String pluginName);
	
}