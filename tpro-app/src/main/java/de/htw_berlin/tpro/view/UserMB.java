package de.htw_berlin.tpro.view;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.controller.Navigation;
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

    private User currentUser;
    
	public String login() {
		currentUser = userService.login(credentials.getUsername(), credentials.getPassword());
		if (currentUser != null) {
			message.addMessage(FacesMessage.SEVERITY_INFO, "Anmeldung erfolgreich! Willkommen bei TPro :)");
			return (isLoggedInAsAdmin()) ? navigation.goToAdminHomePage() : navigation.goToUserHomePage();
		}
		message.addMessage(FacesMessage.SEVERITY_ERROR, "Anmeldung fehgeschlagen! Versuchen Sie es erneut ;)");
		return null;
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
    	return (currentUser != null) ? userService.userIsAuthorized(currentUser.getUsername(), "admin", "tpro") : false;
    }
    
    public boolean isAuthorized(String role, String context) {
    	return (currentUser != null) ? userService.userIsAuthorized(currentUser.getUsername(), role, context) : false;
    }
    
    @Produces @LoggedIn 
    public User getCurrentUser() {
        return currentUser;
    }

}
