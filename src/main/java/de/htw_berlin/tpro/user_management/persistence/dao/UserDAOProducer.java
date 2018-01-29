package de.htw_berlin.tpro.user_management.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class UserDAOProducer {
	
	final Logger logger = LoggerFactory.getLogger(UserDAOProducer.class);
	private static UserDAO userDAO = new UserDAO();

	@Produces @DefaultUserDAO
	public UserDAO getUserDAO() {
		logger.info("Produced UserDAO instance !!!");
		return userDAO;
	}
	
}
