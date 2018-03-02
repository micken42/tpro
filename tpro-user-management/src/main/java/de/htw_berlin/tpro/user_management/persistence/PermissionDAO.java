package de.htw_berlin.tpro.user_management.persistence;

import de.htw_berlin.tpro.user_management.model.Permission;

public class PermissionDAO extends GenericDAO<Permission> {
	
	private static final long serialVersionUID = 1L;

	public PermissionDAO() {
		super(Permission.class);
	}

}
