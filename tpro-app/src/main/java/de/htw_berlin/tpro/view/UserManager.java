package de.htw_berlin.tpro.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.user_management.annotation.LoggedIn;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.persistence.DefaultUserFacade;
import de.htw_berlin.tpro.user_management.persistence.UserFacade;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class UserManager implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject @LoggedIn
    private User currentUser;
	
	@Inject @DefaultUserFacade
	private UserFacade userFacade;
	
	private @Getter List<User> users;
	private @Getter @Setter List<User> filteredUsers;
	
	@PostConstruct
	void initializeUsers() {
		users = userFacade.getAllUsers();
		filteredUsers = users;
	}

}
