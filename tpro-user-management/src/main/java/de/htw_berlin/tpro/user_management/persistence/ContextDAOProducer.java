package de.htw_berlin.tpro.user_management.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ContextDAOProducer {
	
	private static ContextDAO contextDAO = new ContextDAO();

	@Produces @DefaultContextDAO
	public ContextDAO getContextDAO() {
		return contextDAO;
	}
	
}
