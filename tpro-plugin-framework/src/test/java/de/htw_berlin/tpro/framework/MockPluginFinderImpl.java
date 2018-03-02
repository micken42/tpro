package de.htw_berlin.tpro.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.persistence.ContextFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultContextFacade;
import lombok.Getter;

@Dependent
@DefaultPluginFinder
public class MockPluginFinderImpl implements PluginFinder {
	private static final long serialVersionUID = 1L;

	private @Getter HashMap<String, Plugin> plugins;
	
	@Inject @DefaultContextFacade
	ContextFacade contextFacade;
	
	@Override
	public Map<String, Plugin> findAndInititalizePlugins() {
		plugins = new HashMap<String, Plugin>();
		createPlugin(getExamplePlugin1ConfigInfo());
		createPlugin(getExamplePlugin2ConfigInfo());
		return plugins;
	}
	
	private HashMap<String, String> getExamplePlugin1ConfigInfo() {
		HashMap<String, String> configInfo = new HashMap<String, String>();
		configInfo.put("author", "Michael Baumert"); 
		configInfo.put("name", "example-plugin-1"); 
		configInfo.put("version", "1.0"); 
		configInfo.put("title", "Example Plugin 1"); 
		configInfo.put("description", "This is an example for a plugin."); 
		configInfo.put("thumbnail", "path/to/png"); 
		configInfo.put("permissions", "provider, consumer"); 
	  	return configInfo;
	}
	
	private HashMap<String, String> getExamplePlugin2ConfigInfo() {
		HashMap<String, String> configInfo = new HashMap<String, String>();
		configInfo.put("author", "Michael Baumert"); 
		configInfo.put("name", "example-plugin-2"); 
		configInfo.put("version", "2.0"); 
		configInfo.put("title", "Example Plugin 2"); 
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
    	
    	Context persistentContext = contextFacade.getContextByName(pluginContext.getName());
    	if (persistentContext == null) {
    		contextFacade.saveContext(pluginContext);
    	} else {
    		addMissingPermissionFromNewContextToPeristentContext(pluginContext, persistentContext);
    		contextFacade.updateContext(persistentContext);
    		pluginContext = persistentContext;
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
    
    private void addMissingPermissionFromNewContextToPeristentContext(Context pluginContext,
			Context persistentContext) {
		for (Permission permission : pluginContext.getPermissions()) {
			if (!permissionExistsInContext(permission, persistentContext)) {
				persistentContext.addPermission(permission);
			}
		}
	}

	private boolean permissionExistsInContext(Permission permission, Context persistentContext) {
		for (Permission existingPermission : persistentContext.getPermissions()) {
			if (existingPermission.getName().equals(permission.getName())) {
				return true;
			}
		}
		return false;
	}
	
}
