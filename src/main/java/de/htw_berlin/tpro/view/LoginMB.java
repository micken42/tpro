package de.htw_berlin.tpro.view;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserManagement;
import de.htw_berlin.tpro.user_management.service.UserManagementService;

@Named
@SessionScoped
public class LoginMB implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//@Inject @DefaultUserFacade
	//UserFacadeImpl ufi;
	
	@Inject
    CredentialsMB credentials;
	@Inject @DefaultUserManagement
	UserManagementService userService;

    private User currentUser;
    
	public String login() {
		currentUser = userService.login(credentials.getUsername(), credentials.getPassword());
		if (currentUser != null)
			return (isAdmin()) ? "admin" : "user";
		return null;
	}
    
	public String signUp() {
		currentUser = userService.signUp(credentials.getUsername(),credentials.getPassword());
	    // TODO Message "successfully signed up!"
		return (currentUser != null) ? "success" :  null;
	}
    
    public String logout() {
        currentUser = null;
        return "success";
    }
    
    public boolean isLoggedIn() {
       return currentUser != null;
    }
    
    public boolean isAdmin() {
    	return userService.userIsAuthorized(currentUser, "admin", "tpro");
    }
    
    @Produces @LoggedIn User getCurrentUser() {
        return currentUser;
    }

}
