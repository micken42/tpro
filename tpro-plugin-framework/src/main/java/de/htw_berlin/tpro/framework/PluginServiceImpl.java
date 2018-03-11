package de.htw_berlin.tpro.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.Startup;

import de.htw_berlin.tpro.user_management.model.Role;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;

@Named(value="pluginService")
@Startup
@ApplicationScoped
@DefaultPluginService
public class PluginServiceImpl implements PluginService
{
	private static final long serialVersionUID = 1L;

	private HashMap<String, Plugin> plugins;
	
	@Inject @DefaultUserService
	UserService userService; 
	
	@Inject @DefaultPluginFinder
	PluginFinder pluginFinder;

	@PostConstruct
	void initializePlugins() {
		plugins = (HashMap<String, Plugin>) pluginFinder.findAndInititalizePlugins();
	}

	@Override
	public List<Plugin> getAllPlugins() {
		List<Plugin> result = new ArrayList<Plugin>();
		for (Object plugin : plugins.values().toArray()) 
			result.add((Plugin) plugin);
		return result;
	}

	@Override
	public Plugin getPluginByName(String pluginName) {
		return plugins.get(pluginName);
	}

	/**
	 * Wenn ein Benutzer mit dem uebergebenen Benutzernamen 
	 * und ein Dienst mit dem übergebenen Dienstnamen existiert,
	 * dann wird geprüft, ob der Benutzer alle Rollen im Dienst besitzt.
	 *  
	 * @param username Benutzername des Benutzers
	 * @param pluginName Name des Dienstes
	 * @return true, wenn die Bedingung erfüllt ist
	 */
	@Override
	public boolean userIsPluginProvider(String username, String pluginName) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return false;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles) {
			if(!userService.userIsAuthorized(username, role.getName(), pluginName)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Wenn ein Benutzer mit dem uebergebenen Benutzernamen 
	 * und ein Dienst mit dem übergebenen Dienstnamen existiert,
	 * dann wird geprüft, ob der Benutzer mind. eine Rolle im Dienst hat.
	 *  
	 * @param username Benutzername des Benutzers
	 * @param pluginName Name des Dienstes
	 * @return true, wenn die Bedingung erfüllt ist und der Benutzer somit
	 * 		   als Dienstkonsument gilt.
	 */
	@Override
	public boolean userIsPluginConsumer(String username, String pluginName) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return false;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles) {
			if(userService.userIsAuthorized(username, role.getName(), pluginName))
				return true;
		}
		return false;
	}
	
	/**
	 * Wenn ein Benutzer mit dem uebergebenen Benutzernamen existiert,
	 * dann wird geprüft in welchen Diensten der Benutzer alle Rollen besitzt.
	 * Alle Dienste für die der genannte Bedingung erfuellt wurde, 
	 * werden als die anbietbare Dienste des Benutzers zurueckgeliefert.
	 * 
	 * @param username Benutzername des Benutzers
	 * @return anbietbare Dienste des Benutzers
	 */
	@Override
	public List<Plugin> getPluginsProvidableByUserWithUsername(String username) {
		List<Plugin> allPlugins = getAllPlugins();
		if (allPlugins == null) 
			return null;
		List<Plugin> plugins = new ArrayList<Plugin>();
		for (Plugin plugin : allPlugins) {
			if (userIsPluginProvider(username, plugin.getName()))
				plugins.add(plugin);
		}
		return (plugins.size() != 0) ? plugins : null;
	}
	
	/**
	 * Wenn ein Benutzer mit dem uebergebenen Benutzernamen existiert,
	 * dann wird geprüft in welchen Diensten der mindestens eine Rolle besitzt.
	 * Alle Dienste für die die genannte Bedingung erfuellt wurde, 
	 * werden als die verfuegbare, bzw. konsumierbare Dienste des Benutzers zurueckgeliefert 
	 * 
	 * @param username Benutzername des Benutzers 
	 * @return anbietbare Dienste des Benutzers
	 */
	@Override
	public List<Plugin> getPluginsAccessableByUserWithUsername(String username) {
		List<Plugin> allPlugins = getAllPlugins();
		if (allPlugins == null) 
			return null;
		List<Plugin> plugins = new ArrayList<Plugin>();
		for (Plugin plugin : allPlugins) {
			if (userIsPluginConsumer(username, plugin.getName()))
				plugins.add(plugin);
		}
		return (plugins.size() != 0) ? plugins : null;
	}

	/**
	 * Wenn ein Benutzer mit dem uebergebenen Benutzernamen 
	 * und ein Dienst mit dem übergebenen Dienstnamen existiert,
	 * dann werden dem Benutzer alle Rollen des Dienstes zugwiesen.
	 *  
	 * @param username Benutzername des Benutzers
	 * @param pluginName Name des Dienstes
	 */
	@Override
	public void assignUserToPluginAsPluginProvider(String pluginName, String username) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles) 
			userService.authorizeUser(username, role.getName(), pluginName);
	}
	
	/**
	 * Es wird geprüft ob ein Dienst mit dem übergebenen Dienstnamen existiert und
	 * fuer jeden Benutzer in den uebergebenen Benutzernamen wird geprueft, ob dieser existiert.
	 * Sind beide Bedinungen erfüllt, werden jedem existierenden Benutzer  alle Rollen des Dienstes zugwiesen.
	 *  
	 * @param username Benutzername des Benutzers
	 * @param pluginName Name des Dienstes
	 */
	@Override
	public void assignUsersToPluginAsPluginProviders(String pluginName, List<String> usernames) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (String username : usernames) 
			pluginRoles.forEach(role -> userService.authorizeUser(username, role.getName(), pluginName));
	}

	/**
	 * Wenn eine Gruppe mit dem uebergebenen Gruppennamen 
	 * und ein Dienst mit dem übergebenen Dienstnamen existiert,
	 * dann werden der Gruppe alle Rollen des Dienstes zugwiesen.
	 *  
	 * @param groupName Name der Gruppe
	 * @param pluginName Name des Dienstes
	 */
	@Override
	public void assignGroupToPluginAsPluginProviderGroup(String pluginName, String groupName) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles)
			userService.authorizeGroup(groupName, role.getName(), pluginName);
	}

	/**
	 * Wenn ein Benutzer mit dem uebergebenen Benutzernamen 
	 * und ein Dienst mit dem übergebenen Dienstnamen existiert,
	 * dann werden dem Benutzer alle Rollen des Dienstes entzogen, 
	 * sofern dieser auch welche besitzt. Zudem wird er aus allen Gruppen entfernt, 
	 * die Rollen im Dienst besitzen, damit dieser nicht weiterhin im Dienst berechtigt ist, 
	 * weil er über seine Gruppen authorisiert wird.
	 *  
	 * @param username Benutzername des Benutzers
	 * @param pluginName Name des Dienstes
	 */
	@Override
	public void removeUserFromPlugin(String pluginName, String username) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles) 
			userService.deauthorizeUser(username, role.getName(), pluginName);
	}

	/**
	 * Es wird geprüft ob ein Dienst mit dem übergebenen Dienstnamen existiert und
	 * fuer jeden Benutzer in den uebergebenen Benutzernamen wird geprueft, ob dieser existiert.
	 * Sind beide Bedinungen erfüllt, werden jedem existierenden Benutzer alle Rollen des Dienstes entzogen,
	 * sofern dieser welche besitzt. Zudem wird er aus allen Gruppen entfernt, die Rollen im Dienst
	 * besitzen, damit dieser nicht weiterhin im Dienst berechtigt ist, weil er über seine Gruppen 
	 * authorisiert wird.
	 *  
	 * @param username Benutzername des Benutzers
	 * @param pluginName Name des Dienstes
	 */
	@Override
	public void removeUsersFromPlugin(String pluginName, List<String> usernames) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (String username : usernames) 
			pluginRoles.forEach(role -> userService.deauthorizeUser(username, role.getName(), pluginName));
	}

	/**
	 * Wenn eine Gruppe mit dem uebergebenen Gruppennamen 
	 * und ein Dienst mit dem übergebenen Dienstnamen existiert,
	 * dann werden der Gruppe alle Rollen des Dienstes entzogen, 
	 * sofern in dieser auch welche vorhanden sind.
	 *  
	 * @param username Benutzername des Benutzers
	 * @param pluginName Name des Dienstes
	 */
	@Override
	public void removeGroupsFromPlugin(String pluginName, String groupName) {
		Plugin plugin = getPluginByName(pluginName);
		if (plugin == null)
			return;
		Set<Role> pluginRoles = plugin.getRoles();
		for (Role role : pluginRoles)
			userService.deauthorizeGroup(groupName, role.getName(), pluginName);
	}

	/**
	 * Wenn ein Dienst mit dem uebergebenen Dienstnamen existiert,  
	 * dann werden alle Benutzer zurueckgeliefert, die alle Rollen 
	 * im Dienst besitzen und somit als Dienstanbieter gelten.
	 *  
	 * @param pluginName Name des Dienstes
	 * @return Alle Dienstanbieter des Dienstes
	 */
	@Override
	public List<User> getAllPluginProvidersByPluginName(String pluginName) {
		List<User> providers = new ArrayList<User>();
		for (User user : userService.getAllUsers()) {
			if (userIsPluginProvider(user.getUsername(), pluginName))
				providers.add(user);
		}
		return (providers.size() != 0) ? providers : null;
	}

	/**
	 * Wenn ein Dienst mit dem uebergebenen Dienstnamen existiert,  
	 * dann werden alle Benutzer zurueckgeliefert, die mind. eine Rolle 
	 * im Dienst besitzen und somit als Dienstkonsumenten gelten. 
	 * Dienstkonsumenten schließen Dienstanbieter mit ein.
	 *  
	 * @param pluginName Name des Dienstes
	 * @return Alle Dienstanbieter des Dienstes
	 */
	@Override
	public List<User> getAllUsersWithAccessToPlugin(String pluginName) {
		List<User> consumers = new ArrayList<User>();
		for (User user : userService.getAllUsers()) {
			if (userIsPluginConsumer(user.getUsername(), pluginName))
				consumers.add(user);
		}
		return (consumers.size() != 0) ? consumers : null;
	}
	
}