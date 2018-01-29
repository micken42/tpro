package de.htw_berlin.tpro.user_management.service;

import javax.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DefaultUserManagementServiceProducer {

	final Logger logger = LoggerFactory.getLogger(DefaultUserManagementServiceProducer.class);
	
	private static DefaultUserManagementService userService = new DefaultUserManagementService();

	//@Produces @DefaultUserManagement
	public DefaultUserManagementService getUserManagementService() {
		logger.info("Produced DefaultUserManagementService Instance -> " + userService.toString());
		return userService;
	}
	
}
