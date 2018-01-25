package de.htw_berlin.tpro.users.mb;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import de.htw_berlin.tpro.users.model.User;
import de.htw_berlin.tpro.users.model.UserList;

@Named
@ApplicationScoped
public class UserService implements Serializable {
	private static final long serialVersionUID = 1L;

	private UserList users;
	
	public UserService() {
		users = new UserList();
	}
	
	public User getUser(String username, String password) {
		for (User user: users.getUsers()) {
			if (!user.getUsername().equals(username))
				continue;
			if (!user.getPassword().equals(password))
				break;
			return user;
		}
		return null;
	}

}