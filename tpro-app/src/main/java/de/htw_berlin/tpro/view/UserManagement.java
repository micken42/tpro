package de.htw_berlin.tpro.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.controller.Navigation;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;
import lombok.Getter;

@Named
@RequestScoped
public class UserManagement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject 
	Message message;
	
	@Inject
	Navigation navigation;

	@Inject @DefaultUserService
	UserService userService;

	@Inject
    Credentials credentials;
	private @Getter List<User> users;
	
	@PostConstruct
	void init() {
		users = userService.getAllUsers();
	}
	
	public String addUser() {
		// create new user from credential
		User user = new User(credentials.getPrename(), credentials.getSurname(), credentials.getEmail(), 
							 credentials.getUsername(), credentials.getPassword());
		// save group with users
		try {
			userService.signUp(user);
			message.addMessage(FacesMessage.SEVERITY_INFO, "Anlegen erfolgreich!"
					+ " Der Benutzer \"" + user.getUsername() + "\" wurde angelegt :)");
		} catch (Exception e) {
			message.addMessage(FacesMessage.SEVERITY_ERROR, "Anlegen fehlgeschlagen!"
					+ " Es ist ein Fehler aufgetreten :(");
		}
		return navigation.goToUserManagementPage();
	}
	
	public String removeUser(User user) {
		if (user == null) 
			return null;
		try {
			userService.deleteUserByUsername(user.getUsername());
			message.addMessage(FacesMessage.SEVERITY_INFO, "Löschen erfolgreich!"
					+ " Der Benutzer \"" + user.getUsername() + "\" wurde gelöscht :)");
		} catch (Exception e) {
			message.addMessage(FacesMessage.SEVERITY_ERROR, "Löschen fehlgeschlagen!"
					+ " Der Benutzer \"" + user.getUsername() + "\" konnte nicht gelöscht werden :(");
		}
		return navigation.goToUserManagementPage();
	}
}
