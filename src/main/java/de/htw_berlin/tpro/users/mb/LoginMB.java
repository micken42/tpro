package de.htw_berlin.tpro.users.mb;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.users.annotation.LoggedIn;
import de.htw_berlin.tpro.users.model.User;

@SessionScoped 
@Named
public class LoginMB implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
    private CredentialsMB credentials;
	@Inject
	private UserService userService;
    	
    private User currentUser;

	public String login() {
		currentUser = userService.getUser(credentials.getUsername(), credentials.getPassword());
		return (currentUser != null) ? "success" : "failure";
	}
    
    public void logout() {
        currentUser = null;
    }
    
    public boolean isLoggedIn() {
       return currentUser != null;
    }
    
    @Produces @LoggedIn User getCurrentUser() {
        return currentUser;
    }

}
