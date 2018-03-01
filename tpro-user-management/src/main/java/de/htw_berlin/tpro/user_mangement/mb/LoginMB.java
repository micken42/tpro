package de.htw_berlin.tpro.user_mangement.mb;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserManagement;
import de.htw_berlin.tpro.user_management.service.UserManagementService;

@Named
@SessionScoped
public class LoginMB implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
    Credentials credentials;
	@Inject @DefaultUserManagement
	UserManagementService userService;

    private User currentUser;
    
//    // TEST STUFF BEGIN
//	@Inject @DefaultUserFacade
//	UserFacade userFacade;
//	@Inject @DefaultPermissionFacade
//	PermissionFacade permissionFacade;
//	// TEST STUFF END
    
	public String login() {
		currentUser = userService.login(credentials.getUsername(), credentials.getPassword());
		if (currentUser != null) {
//			// TEST STUFF BEGIN
//			Permission permission = permissionFacade.getPermissionByPermissionAndContextName("publisher", "bookstore");
//			currentUser.addPermission(permission);
//			userFacade.updateUser(currentUser);
//			// TEST STUFF END
			addMessage(FacesMessage.SEVERITY_INFO, "Anmeldung Erfolgreich! Willkommen bei TPro :)");
			return (isLoggedInAsAdmin()) ? "/admin/plugin-management?faces-redirect=true" : "/user/dashboard?faces-redirect=true";
		}
		return null;
	}
    
	public String signUp() {
		User newUser = new User(credentials.getUsername(), credentials.getPassword());
		newUser.setPrename(credentials.getPrename());
		newUser.setSurname(credentials.getSurname());
		newUser.setEmail(credentials.getEmail());
		try {
			currentUser = userService.signUp(newUser);
		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR , "Ein Fehler ist bei der Registrierung aufgetreten. Versuchen Sie es mit einem anderen Benutzernamen erneut.");
		}
		if (currentUser != null) {
			return login();
		}
		return null;
	}
    
    public String logout() {
        currentUser = null;
        addMessage(FacesMessage.SEVERITY_INFO, "Abmeldung erfolgreich! Auf Wiedersehen ;)");
        return "/index?faces-redirect=true";
    }
    
    public boolean isLoggedIn() {
       return currentUser != null;
    }
    
    public boolean isLoggedInAsAdmin() {
    	return userService.userIsAuthorized(currentUser, "admin", "tpro");
    }
    
    public boolean isAuthorized(String permission, String context) {
    	return userService.userIsAuthorized(currentUser, permission, context);
    }
    
    @Produces @LoggedIn User getCurrentUser() {
        return currentUser;
    }
    
    // TESTS
     
    public void addMessage(Severity level, String message) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	FacesMessage msg = new FacesMessage(level, message,  null);
        context.addMessage(null, msg);
    	context.getExternalContext().getFlash().setKeepMessages(true);
    }

}
