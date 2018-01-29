package de.htw_berlin.tpro.user_management.persistence.facade;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class PermissionFacadeImplProducer {

	private static PermissionFacadeImpl permissionFacade = new PermissionFacadeImpl();

	@Produces @DefaultPermissionFacade
	public PermissionFacadeImpl getPermissionFacade() {
		return permissionFacade;
	}
	
}
