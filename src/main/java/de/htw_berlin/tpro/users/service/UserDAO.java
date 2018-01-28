package de.htw_berlin.tpro.users.service;

import java.io.Serializable;

import de.htw_berlin.tpro.users.model.User;

public class UserDAO extends GenericDAO<User> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public UserDAO() {
		super(User.class);
	}

}
