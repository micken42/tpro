package de.htw_berlin.tpro.framework;

import java.io.Serializable;
import java.util.Map;

public interface PluginFinder extends Serializable {

	Map<String, Plugin>  findAndInititalizePlugins();
	
	void createPlugin(Map<String,String> pluginConfigInfo);
}
