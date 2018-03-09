package de.htw_berlin.tpro.user_management.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RoleDAOProducer {

	private static RoleDAO roleDAO = new RoleDAO();

	@Produces @DefaultRoleDAO
	public RoleDAO getRoleDAO() {
		return roleDAO;
	}
	
}
