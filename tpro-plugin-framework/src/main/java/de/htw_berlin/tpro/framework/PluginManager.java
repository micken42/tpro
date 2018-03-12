package de.htw_berlin.tpro.framework;

import java.io.Serializable;
import java.util.Map;

public interface PluginManager extends Serializable {

	Map<String, Plugin> getAllPlugins();
	
	Plugin getPluginByName(String pluginName);
	
	void createPlugin(Map<String,String> pluginConfigInfo);
}
