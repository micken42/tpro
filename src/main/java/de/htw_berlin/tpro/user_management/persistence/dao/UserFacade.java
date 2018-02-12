package de.htw_berlin.tpro.user_management.persistence.dao;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.user_management.model.User;

public interface UserFacade extends Serializable {

	public void updateAllUsers(List<User> users);
	
	public void updateUser(User user);
	
	public void saveUser(User user);
	
	public List<User> getAllUsers();
	
	public User getUserByUsername(String username);

	public List<String> getAllUsernames();
		
}
