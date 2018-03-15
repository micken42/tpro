package de.htw_berlin.tpro.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.controller.Navigation;
import de.htw_berlin.tpro.framework.DefaultPluginService;
import de.htw_berlin.tpro.framework.Plugin;
import de.htw_berlin.tpro.framework.PluginService;
import de.htw_berlin.tpro.user_management.annotation.LoggedIn;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;

@Named
@SessionScoped
public class UserMB implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	Navigation navigation;
	@Inject
	Message message;
	
	@Inject
    Credentials credentials;
	@Inject @DefaultUserService
	UserService userService;
	@Inject @DefaultPluginService
	PluginService pluginService;

    private User currentUser;
    private boolean isAdmin = false;
    private List<Plugin> allUserPlugins = new ArrayList<Plugin>(); 
    private List<Plugin> providableUserPlugins = new ArrayList<Plugin>(); ;
    
	public String login() {
		currentUser = userService.login(credentials.getUsername(), credentials.getPassword());
		if (currentUser != null) {
			initCurrentUser();
			message.addMessage(FacesMessage.SEVERITY_INFO, "Anmeldung erfolgreich! Willkommen bei TPro :)");
			return isAdmin ? navigation.goToAdminHomePage() : navigation.goToUserHomePage();
		}
		message.addMessage(FacesMessage.SEVERITY_ERROR, "Anmeldung fehgeschlagen! Versuchen Sie es erneut ;)");
		return null;
	}
	
	public void initCurrentUser() {
		isAdmin = userService.userIsAuthorized(currentUser.getUsername(), "admin", "tpro");
		allUserPlugins = isAdmin ?  pluginService.getAllPlugins() 
				: pluginService.getPluginsAccessableByUserWithUsername(currentUser.getUsername());
		providableUserPlugins = isAdmin ?  new ArrayList<Plugin>()
				: pluginService.getPluginsProvidableByUserWithUsername(currentUser.getUsername());
	}
    
	public String signUp() {
		User newUser = new User(credentials.getPrename(), credentials.getSurname(), credentials.getEmail(), 
								credentials.getUsername(), credentials.getPassword());
		try {
			currentUser = userService.signUp(newUser);
		} catch (Exception e) {
			message.addMessage(FacesMessage.SEVERITY_ERROR , "Ein Fehler ist bei der Registrierung aufgetreten. "
					+ "Versuchen Sie es mit einem anderen Benutzernamen erneut.");
		}
		if (currentUser != null) {
			return login();
		}
		return null;
	}
    
    public String logout() {
        currentUser = null;
        message.addMessage(FacesMessage.SEVERITY_INFO, "Abmeldung erfolgreich! Auf Wiedersehen ;)");
        return "/index?faces-redirect=true";
    }
    
    public boolean isLoggedIn() {
       return currentUser != null;
    }
    
    public boolean isLoggedInAsAdmin() {
    	return isAdmin;
    }
    
    public boolean isAuthorized(String role, String context) {
    	return (currentUser != null) ? userService.userIsAuthorized(currentUser.getUsername(), role, context) : false;
    }
    
    public List<Plugin> getPlugins() {
    	return allUserPlugins;
    }
    
    public List<Plugin> getProvidablePlugins() {
    	return providableUserPlugins;
    }
    
    public boolean canBeProvided(Plugin plugin) {
    	return (providableUserPlugins != null) ? providableUserPlugins.contains(plugin) : false;
    }
    
    @Produces @LoggedIn 
    public User getCurrentUser() {
        return currentUser;
    }

//  /**
//   * Nuetzlich, falls Plugins zur Laufzeit hinzugefügt werden, während eine Session geoeffnet ist
//   */
//  public List<Plugin> getRefreshedPlugins() {
//  	allUserPlugins = isAdmin ? pluginService.getAllPlugins() 
//				: pluginService.getPluginsAccessableByUserWithUsername(currentUser.getUsername());
//  	return allUserPlugins;
//  }

}
