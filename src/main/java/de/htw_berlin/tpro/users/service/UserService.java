package de.htw_berlin.tpro.users.service;

import de.htw_berlin.tpro.users.model.User;

public interface UserService {
	public User getUser(String username, String password);
}
