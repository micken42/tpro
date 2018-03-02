package de.htw_berlin.tpro.user_management.persistence;

import de.htw_berlin.tpro.user_management.model.Group;

public class GroupDAO extends GenericDAO<Group> {
	
	private static final long serialVersionUID = 1L;

	public GroupDAO() {
		super(Group.class);
	}

}
