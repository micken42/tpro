package de.htw_berlin.tpro.user_management.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class UserDAOProducer {
	
	private static UserDAO userDAO = new UserDAO();

	@Produces @DefaultUserDAO
	public UserDAO getUserDAO() {
		return userDAO;
	}
	
}
