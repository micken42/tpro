package de.htw_berlin.tpro.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Role;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;
import lombok.Getter;

@Dependent
@DefaultPluginManager
public class PluginManagerImpl implements PluginManager {
	private static final long serialVersionUID = 1L;

	private @Getter HashMap<String, Plugin> plugins;
	
	@Inject @DefaultUserService
	UserService userService;

	public PluginManagerImpl() {
		plugins = new HashMap<String, Plugin>();;
	}
	
	/**
	 * In allen JAR Datein, die sich im CLASSPATH befinden wird, nach JARs gesucht,
	 * die eine META-INF/PLUGIN_MANIFEST* Datei enthalten.  
	 * Ist eine enthalten, wird die Plugin Konfigurationsdatei gelesen und versucht 
	 * ein Plugin anhand dieser zu erstellen.
	 */
	@PostConstruct
	public void findAndInititalizePlugins() {
		plugins = new HashMap<String, Plugin>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = ((URLClassLoader) cl).getURLs();
		for (URL url : urls) {
			File f = new File(url.getPath());
			if (!f.isDirectory()) {
				try (InputStream urlIn = url.openStream(); JarInputStream jarIn = new JarInputStream(urlIn)) {
					JarEntry entry;
					while ((entry = jarIn.getNextJarEntry()) != null) {
						if (entry.getName().startsWith("META-INF/PLUGIN_MANIFEST")) {
							createPlugin(getPluginConfigInfo(url, entry));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * MANIFEST parsing und Rückgabe einer HashMap mit den Manifest Werten in Form von Key Value Pairs
	 */
	private Map<String, String> getPluginConfigInfo(URL url, JarEntry entry) throws IOException {
		JarFile jarFile = new JarFile(url.getPath());
		InputStream input = jarFile.getInputStream(entry);
		InputStreamReader isr = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isr);
		HashMap<String,String> result = new HashMap<String, String>();
		String line;
		while ((line = reader.readLine()) != null) {
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
		reader.close();
		jarFile.close();
		return result;
	}
	
	/**
	 * Initialisiert ein Plugin mit Hilfe der Übergebenen Map, falls diese vom PluginConfigInfoValidator als valide erkannt wird.
	 * Ist die Config valide, wird das Plugin initialisiert, dessen Rollen in der Benutzerverwaltung eingefügt und dieses mit 
	 * seinem Namen in der plugins Map gespeichert. Das Einfügen der Rollen in der Benutzerverwaltung geschieht in einem neuen 
	 * Kontext (oder einem Existierenden), der den Namen des Plugins trägt. Wenn gleichnamiges Plugin bereits existiert wird kein
	 * neues Plugin initialisiert und gespeichert.
	 */
	@Override
	public void createPlugin(Map<String,String> pluginConfigInfo)  // TODO: Throw specific exceptions when pluginConfigInfo is invalid?
    {
    	if (!PluginConfigInfoValidator.isValid(pluginConfigInfo)) 
    		return;
    		/* TODO: Exceptions or not?? 
    		 	throw new FrameworkException("Plugin Configuration is invalid. Check if META-INF/PLUGIN_MANIFEST.MF in plugin exists "
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
    	List<String> roleNames = 
    			PluginConfigInfoValidator.getRoleNamesFromCommaSeperatedRolesValue(pluginConfigInfo.get("roles"));
    	for (String roleName : roleNames) {
    		Role role = new Role(roleName, pluginContext);
    		pluginContext.addRole(role);
    	}
    	
    	Context persistentContext = userService.getContextByName(pluginContext.getName());
    	if (persistentContext == null) {
    		userService.saveContext(pluginContext);
    	} else {
    		addMissingRoleFromNewContextToPeristentContext(pluginContext, persistentContext);
    		userService.updateContext(persistentContext);
    		pluginContext = persistentContext;
    	}
    	
    	Plugin plugin = new DefaultPlugin(pluginAuthor, 
						    			  pluginName, 
						    			  pluginVersion, 
						    			  pluginTitle, 
						    			  pluginDescription, 
						    			  pluginThumbnailResource, 
						    			  pluginContext,
						    			  pluginContext.getRoles()
						    			  );
        System.out.println(" - - - Add Plugin \"" + plugin.getName() + "\" to TPro");
        System.out.println(" - - - - - - - ConfigInfo: " + pluginConfigInfo);
        
        plugins.put(plugin.getName(), plugin);
    }
    
    private void addMissingRoleFromNewContextToPeristentContext(Context pluginContext,
			Context persistentContext) {
		for (Role role : pluginContext.getRoles()) {
			if (!roleExistsInContext(role, persistentContext)) {
				persistentContext.addRole(role);
			}
		}
	}

	private boolean roleExistsInContext(Role role, Context persistentContext) {
		for (Role existingRole : persistentContext.getRoles()) {
			if (existingRole.getName().equals(role.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, Plugin> getAllPlugins() {
		return plugins;
	}

	@Override
	public Plugin getPluginByName(String pluginName) {
		return plugins.get(pluginName);
	}
	
}
