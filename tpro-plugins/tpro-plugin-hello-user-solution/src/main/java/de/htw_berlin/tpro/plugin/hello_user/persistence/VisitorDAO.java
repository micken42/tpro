package de.htw_berlin.tpro.plugin.hello_user.persistence;

import de.htw_berlin.tpro.plugin.hello_user.model.Visitor;

public class VisitorDAO extends GenericDAO<Visitor> {

	private static final long serialVersionUID = 1L;

	public VisitorDAO() {
		super(Visitor.class);
	}
}
