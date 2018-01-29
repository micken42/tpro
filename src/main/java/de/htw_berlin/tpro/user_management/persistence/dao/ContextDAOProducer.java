package de.htw_berlin.tpro.user_management.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ContextDAOProducer {
	
	final Logger logger = LoggerFactory.getLogger(ContextDAOProducer.class);
	private static ContextDAO contextDAO = new ContextDAO();

	@Produces @DefaultContextDAO
	public ContextDAO getContextDAO() {
		logger.info("Produced ContextDAO instance !!!");
		return contextDAO;
	}
	
}
