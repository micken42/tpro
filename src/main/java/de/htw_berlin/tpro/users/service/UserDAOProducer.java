package de.htw_berlin.tpro.users.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class UserDAOProducer {

	private static UserDAO userDAO = new UserDAO();

	@Produces @UserData
	public UserDAO getToDoDAO() {
		return userDAO;
	}
	
}
