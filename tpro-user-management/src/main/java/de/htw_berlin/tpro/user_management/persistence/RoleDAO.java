package de.htw_berlin.tpro.user_management.persistence;

import de.htw_berlin.tpro.user_management.model.Role;

public class RoleDAO extends GenericDAO<Role> {
	
	private static final long serialVersionUID = 1L;

	public RoleDAO() {
		super(Role.class);
	}

}
