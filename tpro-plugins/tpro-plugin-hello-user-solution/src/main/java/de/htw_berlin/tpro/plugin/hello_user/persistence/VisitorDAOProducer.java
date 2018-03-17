package de.htw_berlin.tpro.plugin.hello_user.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class VisitorDAOProducer {
	
	private static VisitorDAO visitorDAO = new VisitorDAO();

	@Produces @DefaultVisitorDAO
	public VisitorDAO getVisitorDAO() {
		return visitorDAO;
	}
	
}