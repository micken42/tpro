package de.htw_berlin.tpro.framework;

import java.io.Serializable;
import java.util.List;

public interface PluginManagementService extends Serializable {
	
	List<Plugin> getAllPlugins();
	
	Plugin getPluginByName(String plugin);
	
	List<Plugin> getPluginsByUsername(String username); // TODO: User hat eine Liste seiner pluginnamen pro session
	
	boolean userIsPluginProvider(String username, String plugin); 
	
}