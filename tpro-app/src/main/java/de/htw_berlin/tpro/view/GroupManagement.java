package de.htw_berlin.tpro.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.controller.Navigation;
import de.htw_berlin.tpro.user_management.model.Group;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.service.DefaultUserService;
import de.htw_berlin.tpro.user_management.service.UserService;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class GroupManagement implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject 
	Message message;
	
	@Inject
	Navigation navigation;

	@Inject @DefaultUserService
	UserService userService;
	
	private @Getter List<Group> groups;
	private @Getter @Setter String selectedGroupName;
	
	private @Getter List<User> users;
	private @Getter @Setter List<User> selectedUsers;
	
	@PostConstruct
	void init() {
		users = userService.getAllUsers();
		selectedUsers = new ArrayList<User>();
		groups = userService.getAllGroups();
	}
	
	public String addGroupWithSelectedUsers() {
		if (selectedGroupName == null || selectedUsers == null) 
			return null;
		// create new group with selected name
		Group group = new Group(selectedGroupName);
		// add selected users to the new group
		for (User user : selectedUsers) {
			group.addUser(user);
		}
		// save group with users
		try {
			userService.saveGroup(group);
			message.addMessage(FacesMessage.SEVERITY_INFO, "Anlegen erfolgreich!"
					+ " Die Gruppe \"" + selectedGroupName + "\" wurde angelegt :)");
		} catch (Exception e) {
			message.addMessage(FacesMessage.SEVERITY_ERROR, "Anlegen fehlgeschlagen!"
					+ " Eine Gruppe mit dem Namen existiert bereits :(");
		}
		return navigation.goToGroupManagementPage();
	}
	
	public String removeGroup(Group group) {
		if (group == null) 
			return null;
		try {
			userService.deleteGroupByName(group.getName());
			message.addMessage(FacesMessage.SEVERITY_INFO, "Löschen erfolgreich!"
					+ " Die Gruppe \"" + group.getName() + "\" wurde gelöscht :)");
		} catch (Exception e) {
			message.addMessage(FacesMessage.SEVERITY_ERROR, "Löschen fehlgeschlagen!"
					+ " Die Gruppe \"" + group.getName() + "\" konnte nicht gelöscht werden :(");
		}
		return navigation.goToGroupManagementPage();
	}
	
	public String printGroupUsers(Group group) {
		if (group == null)
			return null;
		String result = null;
		for (User user : group.getUsers()) {
			if (result == null){
				result = user.getUsername();
				continue;
			}
			result += ", " + user.getUsername();
		}
		return result;
	}
}