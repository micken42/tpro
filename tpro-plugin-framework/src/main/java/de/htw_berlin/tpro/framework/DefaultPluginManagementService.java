package de.htw_berlin.tpro.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.Startup;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.persistence.dao.ContextFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.DefaultContextFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.DefaultUserFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.UserFacade;
import de.htw_berlin.tpro.user_management.service.DefaultUserManagement;
import de.htw_berlin.tpro.user_management.service.UserManagementService;

@Named(value="pluginManagementService")
@Startup
@ApplicationScoped
@DefaultPluginManagement
public class DefaultPluginManagementService implements PluginManagementService
{
	private static final long serialVersionUID = 1L;

	private HashMap<String, Plugin> plugins;
	
	@Inject @DefaultContextFacade
    ContextFacade contextFacade;
	
	@Inject @DefaultUserFacade
    UserFacade UserFacade;
	
	@Inject @DefaultUserManagement
	UserManagementService userService; 

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

	@Override
	public List<Plugin> getPluginsByUsername(String username) {
		List<Plugin> result = new ArrayList<Plugin>();
		for (Object value : plugins.values().toArray()) {
			Plugin plugin = (Plugin) value;
			if (userHasPermissionsInPluginContext(username, plugin))
				result.add((Plugin) plugin);
			
		}
		
		return result;
	}

	// TODO: In UserManagementService auslagern???
	private boolean userHasPermissionsInPluginContext(String username, Plugin plugin) {
		for (Permission permission : plugin.getPermissions()) {
			if (UserFacade.getUsersByPermissionAndContextName(permission.getName(), plugin.getContext().getName()) != null)
				return true;
		}
		return false;
	}

	@Override
	public boolean userIsPluginProvider(String username, String plugin) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* * * * * * * * * * * * * * * * * * * * *
	 * TODO: HAS TO BE REFACTORED SOMEDAY!!! *
	 * * * * * * * * * * * * * * * * * * * * */
	
	@PostConstruct
	void inititalizePlugins() {
		plugins = new HashMap<String, Plugin>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = ((URLClassLoader) cl).getURLs();
		for (URL url : urls) {
			System.out.println(url);
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
			System.out.println(line);
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
    	if (!pluginConfigInfoIsValid(pluginConfigInfo)) 
    		throw new FrameworkException("Plugin Configuration is invalid. Check if META-INF/MANIFEST.MF in plugin exists "
    				+ "and is well formed.");
    	if (plugins.get(pluginConfigInfo.get("name")) != null)
    		throw new FrameworkException("Plugin with name \"" + pluginConfigInfo.get("name") + "\" already exists.");
    	
    	String pluginAuthor = pluginConfigInfo.get("author");
    	String pluginName = pluginConfigInfo.get("name");
    	String pluginVersion = pluginConfigInfo.get("version");
    	String pluginTitle = pluginConfigInfo.get("title");
    	String pluginDescription = pluginConfigInfo.get("description");
    	String pluginThumbnailResource = pluginConfigInfo.get("thumbnail");
    	
    	Context pluginContext = new Context(pluginName);
    	List<String> permissionNames = 
    			getPermissionNamesFromCommaSeperatedValue(pluginConfigInfo.get("permissions"));
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
        System.out.println("---addPlugin(" + plugin.getName() + ")");
        System.out.println("---" + pluginConfigInfo);
        
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

	private boolean pluginConfigInfoIsValid(HashMap<String,String> pluginConfigInfo) {
    	if (pluginConfigInfo.get("name") == null)
			return false;
    	return true; // TODO: Prüfe, ob Plugin Manifest Config valide ist !!
    }
    
    private List<String> getPermissionNamesFromCommaSeperatedValue(String permissionsValue) {
    	List<String> permissionNames = new ArrayList<String>();
    	StringTokenizer tokenizer = new StringTokenizer(permissionsValue, ",");
        while (tokenizer.hasMoreTokens()) {
            String value = tokenizer.nextToken().trim();
            permissionNames.add(value);
        }
        return permissionNames;
    }
	
}