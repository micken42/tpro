package de.htw_berlin.tpro.user_management.persistence.facade;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class UserFacadeImplProducer {
	
	final Logger logger = LoggerFactory.getLogger(UserFacadeImplProducer.class);
	private static UserFacadeImpl userFacade = new UserFacadeImpl();

	@Produces @DefaultUserFacade 
	public UserFacadeImpl getUserFacade() {
		logger.info("Produced UserFacadeImplProducer instance !!!");
		return userFacade;
	}
	
}
