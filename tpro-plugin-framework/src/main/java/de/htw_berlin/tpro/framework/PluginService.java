package de.htw_berlin.tpro.framework;

import java.io.Serializable;
import java.util.List;

public interface PluginService extends Serializable {
	
	List<Plugin> getAllPlugins();
	
	Plugin getPluginByName(String pluginName);
	
	List<Plugin> getPluginsProvidableByUserWithUsername(String username);

	List<String> getNamesOfPluginsAcessableByUserWithUsername(String username); // TODO: User hat eine Liste seiner pluginnamen pro session
	
	boolean userIsPluginProvider(String username, String pluginName); 
	
	void assignUserToPluginAsPluginProvider(String pluginName, String username);
	
	void assignUsersToPluginAsPluginProviders(String pluginName, List<String> usernames);

	void assignGroupToPluginAsPluginProviderGroup(String pluginName, String groupName);
	
	void removePluginProviderFromPlugin(String pluginName, String username);
	
	void removePluginProvidersFromPlugin(String pluginName, List<String> usernames);
	
	void removePluginProviderGroupFromPlugin(String pluginName, String groupName);
	
	void removeAllPluginProvidersFromPlugin(String pluginName);
	
}