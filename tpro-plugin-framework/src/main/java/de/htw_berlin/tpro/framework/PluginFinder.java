package de.htw_berlin.tpro.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.persistence.ContextFacade;
import de.htw_berlin.tpro.user_management.persistence.DefaultContextFacade;
import lombok.Getter;

@Dependent
public class PluginFinder implements Serializable {
	private static final long serialVersionUID = 1L;

	private @Getter HashMap<String, Plugin> plugins;
	
	@Inject @DefaultContextFacade
	ContextFacade contextFacade;

	public PluginFinder() {
		plugins = new HashMap<String, Plugin>();;
	}
	
	@PostConstruct
	void inititalizePlugins() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = ((URLClassLoader) cl).getURLs();
		for (URL url : urls) {
			File f = new File(url.getPath());
			if (!f.isDirectory()) {
				try (InputStream urlIn = url.openStream(); JarInputStream jarIn = new JarInputStream(urlIn)) {
					JarEntry entry;
					while ((entry = jarIn.getNextJarEntry()) != null) {
						if (entry.getName().startsWith("META-INF/PLUGIN_MANIFEST")) {
							try {
								readJarEntry(url, entry);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	}
	
	private void readJarEntry(URL url, JarEntry entry) throws IOException {
		JarFile jarFile = new JarFile(url.getPath());
		InputStream input = jarFile.getInputStream(entry);
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);
		HashMap<String,String> result = new HashMap<String, String>();
		String line;
		while ((line = reader.readLine()) != null) {
			//System.out.println(line);
			StringTokenizer st = new StringTokenizer(line, ":");
            if(st.hasMoreTokens())
            {
                String key = st.nextToken().trim();
                if(st.hasMoreTokens())
                {
                    String value = st.nextToken().trim();
                    result.put(key, value);
                }
            }
		}
		try {
			// TODO: Besserer Aufruf des Plugin Erstellungsmechanismuses!!!
			createPlugin(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reader.close();
		jarFile.close();
	}
	
	public void createPlugin(HashMap<String,String> pluginConfigInfo) throws Exception // TODO: Throw specific exceptions when key is missing?!
    {
    	if (!PluginConfigInfoValidator.isValid(pluginConfigInfo)) 
    		return;
    		/* TODO: Exceoptions or not?? 
    		 	throw new FrameworkException("Plugin Configuration is invalid. Check if META-INF/MANIFEST.MF in plugin exists "
    				+ "and is well formed.");*/
    	if (plugins.get(pluginConfigInfo.get("name")) != null)
    		return;
    		//throw new FrameworkException("Plugin with name \"" + pluginConfigInfo.get("name") + "\" already exists.");
    	
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
