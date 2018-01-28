package de.htw_berlin.tpro.users.mb;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.users.annotation.LoggedIn;
import de.htw_berlin.tpro.users.model.Permission;
import de.htw_berlin.tpro.users.model.User;
import de.htw_berlin.tpro.users.service.UserFacade;

@Named
@SessionScoped
public class LoginMB implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
    private CredentialsMB credentials;
	@Inject 
	private UserFacade userFacade;

    private User currentUser;
    
	public String login() {
		currentUser = 
				userFacade.checkCredentials(credentials.getUsername(), credentials.getPassword());
	    return (currentUser != null) ? "success" : null;
	}
    
	public void signUp() {
		// TODO Permissions
		currentUser = new User(credentials.getUsername(),credentials.getPassword(), null);
		userFacade.save(currentUser);
	    // TODO Message "successfully signed up!"
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
