package de.htw_berlin.tpro.user_management.persistence;

import de.htw_berlin.tpro.user_management.model.User;

public class UserDAO extends GenericDAO<User> {

	private static final long serialVersionUID = 1L;

	public UserDAO() {
		super(User.class);
	}

}
