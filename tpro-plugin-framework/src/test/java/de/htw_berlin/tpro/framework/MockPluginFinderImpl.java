package de.htw_berlin.tpro.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Permission;
import lombok.Getter;

@Dependent
@DefaultPluginFinder
public class MockPluginFinderImpl implements PluginFinder {
	private static final long serialVersionUID = 1L;

	private @Getter HashMap<String, Plugin> plugins;
	
	@Override
	public Map<String, Plugin> findAndInititalizePlugins() {
		plugins = new HashMap<String, Plugin>();
		createPlugin(getExamplePluginConfigInfo(1));
		createPlugin(getExamplePluginConfigInfo(2));
		createPlugin(getExamplePluginConfigInfo(3));
		return plugins;
	}
	
	private HashMap<String, String> getExamplePluginConfigInfo(int number) {
		HashMap<String, String> configInfo = new HashMap<String, String>();
		configInfo.put("author", "Michael Baumert"); 
		configInfo.put("name", "example-plugin-" + number); 
		configInfo.put("version", "1.0"); 
		configInfo.put("title", "Example Plugin-" + number); 
		configInfo.put("description", "This is an example for a plugin."); 
		configInfo.put("thumbnail", "path/to/png"); 
		configInfo.put("permissions", "provider, consumer"); 
	  	return configInfo;
	}

	@Override
	public void createPlugin(Map<String,String> pluginConfigInfo)
    {
    	if (!PluginConfigInfoValidator.isValid(pluginConfigInfo)) 
    		return;
    	if (plugins.get(pluginConfigInfo.get("name")) != null)
    		return;
    	
    	String pluginName = pluginConfigInfo.get("name");
    	String pluginAuthor = pluginConfigInfo.get("author");
    	String pluginVersion = pluginConfigInfo.get("version");
    	String pluginTitle = pluginConfigInfo.get("title");
    	String pluginDescription = pluginConfigInfo.get("description");
    	String pluginThumbnailResource = pluginConfigInfo.get("thumbnail");
    	
    	Context pluginContext = new Context(pluginName);
    	List<String> permissionNames = 
    			PluginConfigInfoValidator.getPermissionNamesFromCommaSeperatedPermissionsValue(pluginConfigInfo.get("permissions"));
    	for (String permissionName : permissionNames) {
    		Permission permission = new Permission(permissionName, pluginContext);
    		pluginContext.addPermission(permission);
    	}
    	
    	Plugin plugin = new DefaultPlugin(pluginAuthor, 
						    			  pluginName, 
						    			  pluginVersion, 
						    			  pluginTitle, 
						    			  pluginDescription, 
						    			  pluginThumbnailResource, 
						    			  pluginContext,
						    			  pluginContext.getPermissions()
						    			  );
        System.out.println(" - - - Add Plugin \"" + plugin.getName() + "\" to TPro");
        System.out.println(" - - - - - - - ConfigInfo: " + pluginConfigInfo);
        
        plugins.put(plugin.getName(), plugin);
    }
	
}
