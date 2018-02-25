package de.htw_berlin.tpro.user_management.persistence.dao;

import de.htw_berlin.tpro.user_management.model.Context;

public class ContextDAO extends GenericDAO<Context> {

	private static final long serialVersionUID = 1L;

	public ContextDAO() {
		super(Context.class);
	}
}
