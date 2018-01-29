package de.htw_berlin.tpro.user_management.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class PermissionDAOProducer {

	private static PermissionDAO permissionDAO = new PermissionDAO();

	@Produces @DefaultPermissionDAO
	public PermissionDAO getPermissionDAO() {
		return permissionDAO;
	}
	
}
