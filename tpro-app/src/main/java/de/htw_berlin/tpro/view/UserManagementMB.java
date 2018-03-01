package de.htw_berlin.tpro.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.persistence.dao.DefaultUserFacade;
import de.htw_berlin.tpro.user_management.persistence.dao.UserFacade;
import de.htw_berlin.tpro.user_mangement.mb.LoggedIn;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class UserManagementMB implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject @LoggedIn
    private User currentUser;
	
	@Inject @DefaultUserFacade
	UserFacade userFacade;
	
	@Getter @Setter List<User> users;
	
	@PostConstruct
	void initializeUsers() {
		users = userFacade.getAllUsers();
	}

}
